package auth_service.config;

import auth_service.dto.authenticationDto.request.AuthenticationRequest;
import auth_service.dto.authenticationDto.response.ApiResponse;
import auth_service.exception.AuthenException;
import auth_service.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Slf4j
public class CustomMessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //For Request
    public <T> T decodeAndDeserializeBytes(byte[] message, Class<T> clazz) {
        try {
            System.out.println("requestMessage: " + message.toString());
            // Step 1: Decode the Base64 encoded string
            String rawMessageBody = new String(message, StandardCharsets.UTF_8).replaceAll("\"", "");
            System.out.println("rawMessageBody: " + rawMessageBody);
            String decodedMessage = new String(Base64.getDecoder().decode(rawMessageBody), StandardCharsets.UTF_8);
            System.out.println("Decoded message: " + decodedMessage);

            // Step 2: Deserialize the decoded message to DTO
            ObjectMapper objectMapper = new ObjectMapper();
            T dto = objectMapper.readValue(decodedMessage, clazz);

            System.out.println("DTO: " + dto);
            return dto;

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error: " + e.getMessage());
            throw new AuthenException(ErrorCode.CAN_NOT_DESERIALIZE);
        }
    }


    public void sendErrorMessageToProducer(String correlationId, String replyToQueue, String message, int code) throws JsonProcessingException {
        String res = new ObjectMapper().writeValueAsString
                (ApiResponse.builder()
                        .data(null)
                        .message(message)
                        .code(code)
                        .build());

        rabbitTemplate.convertAndSend(
                "",
                replyToQueue,
                res,
                m -> {
                    m.getMessageProperties().setCorrelationId(correlationId);
                    return m;
                }
        );
    }

    public <T> void sendResponseDataToProducer(String correlationId, String replyToQueue, T data) throws JsonProcessingException {
        // Step 1: Serialize the data to JSON bytes
        System.out.println("resB4Ser: " + data);
        byte[] res = new ObjectMapper().writeValueAsBytes(data);

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

}
