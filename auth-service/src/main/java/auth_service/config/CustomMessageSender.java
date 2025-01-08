package auth_service.config;

import auth_service.config.JWT.CustomJwtDecoder;
import auth_service.dto.authenticationDto.request.AuthenticationRequest;
import auth_service.dto.authenticationDto.response.ApiResponse;
import auth_service.exception.AuthenException;
import auth_service.exception.ErrorCode;
import auth_service.exception.ErrorCodeResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Slf4j
public class CustomMessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    //For Request
    public <T> T decodeAndDeserializeBytes(byte[] message, Class<T> clazz) {
        try {
            // Step 1: Decode the Base64 encoded string
            String rawMessageBody = new String(message, StandardCharsets.UTF_8).replaceAll("\"", "");
            String decodedMessage = new String(Base64.getDecoder().decode(rawMessageBody), StandardCharsets.UTF_8);

            // Step 2: Deserialize the decoded message to DTO
            ObjectMapper objectMapper = new ObjectMapper();
            T dto = objectMapper.readValue(decodedMessage, clazz);

            return dto;

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error: " + e.getMessage());
            throw new AuthenException(ErrorCode.CAN_NOT_DESERIALIZE);
        }
    }


    public void sendErrorCodeToProducer(String correlationId, String replyToQueue, ErrorCode errorCode) throws JsonProcessingException {
        ErrorCodeResponse errorCodeResponse = ErrorCodeResponse.builder().errorCode(errorCode).build();
        sendResponseDataToProducer(correlationId, replyToQueue, errorCodeResponse);
    }

    public <T> void sendResponseDataToProducer(String correlationId, String replyToQueue, T data) throws JsonProcessingException {
        // Step 1: Serialize the data to JSON bytes
//        System.out.println("resB4Ser: " + data);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); //handling LocalDatenew ObjectMapper()

        byte[] res = objectMapper.writeValueAsBytes(data);

        // Step 2: Send the message using RabbitTemplate
        rabbitTemplate.convertAndSend(
                "",
                replyToQueue,
                res,
                message -> {
                    message.getMessageProperties().setContentType("application/json");
                    message.getMessageProperties().setCorrelationId(correlationId);
                    message.getMessageProperties().setReplyTo(replyToQueue); // Dynamic reply queue
                    return message;
                }
        );
    }

    //*Also will decode here
    public Jwt extractTokenFromMessage(Message message) {
        String jwtToken = (String) message.getMessageProperties().getHeaders().get("Authorization");
        if (jwtToken == null) {
            throw new AuthenException(ErrorCode.INVALID_TOKEN);
        }

        Jwt decodedJwt = customJwtDecoder.decode(jwtToken);

        return decodedJwt;
    }

}
