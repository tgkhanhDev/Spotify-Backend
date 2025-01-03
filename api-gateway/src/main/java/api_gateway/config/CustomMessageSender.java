package api_gateway.config;

import api_gateway.config.JWT.CustomJwtDecoder;
import api_gateway.dto.accountDto.response.CheckMailResponse;
import api_gateway.dto.authenticationDto.response.ApiResponse;
import api_gateway.exception.AuthenException;
import api_gateway.exception.ErrorCode;
import api_gateway.exception.ErrorCodeResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Component
@Slf4j
public class CustomMessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    public <T> T decodeAndDeserializeBytes(byte[] message, Class<T> clazz) {
        try {
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

    public <T> T decodeAndDeserializeBytesResponse(byte[] responseMessage, Class<T> clazz) {
        try {
            // Step 1: Convert the byte array to string
            String messageBody = new String(responseMessage, StandardCharsets.UTF_8);

            // Step 2: Deserialize into a generic JsonNode for inspection
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule()); //handling LocalDatenew ObjectMapper()
            JsonNode jsonNode = objectMapper.readTree(messageBody);

            // Step 3: Check if it's an error ErrorCodeResponse
            if (jsonNode.has("errorCode")) {
                ErrorCodeResponse res = objectMapper.treeToValue(jsonNode, ErrorCodeResponse.class);
                throw new AuthenException(res.getErrorCode());
            }

            // Step 4: Deserialize to the specified class
            return objectMapper.readValue(messageBody, clazz);

        } catch (AuthenException e) {
            throw new AuthenException(e.getErrorCode());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error: " + e.getMessage());
            throw new AuthenException(ErrorCode.CAN_NOT_DESERIALIZE);
        }
    }


    //RequestSender
    public <P, R> R customEventSender(String exchange, String routingKey, boolean isRequireToken, P payload, Class<R> responseClass) {
        String correlationId = UUID.randomUUID().toString();
        String replyToQueue = rabbitTemplate.execute(channel -> channel.queueDeclare().getQueue());

        // Serialize payload to bytes
        byte[] payloadSent = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            payloadSent = objectMapper.writeValueAsBytes(payload);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            throw new AuthenException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        // Send the request
        rabbitTemplate.convertAndSend(
                exchange,
                routingKey,
                payloadSent == null ? "" : payloadSent,
                message -> {
                    message.getMessageProperties().setContentType("application/json");
                    message.getMessageProperties().setCorrelationId(correlationId);
                    message.getMessageProperties().setReplyTo(replyToQueue); // Dynamic reply queue
                    if (isRequireToken) {
                        String jwtToken = customJwtDecoder.extractJwtToken();
                        message.getMessageProperties().setHeader("Authorization", jwtToken);
                    }
                    return message;
                }
        );

        // Receive and deserialize the response
        byte[] responseMessage = (byte[]) rabbitTemplate.receiveAndConvert(replyToQueue, 5000); // Wait for up to 5 seconds
        if (responseMessage == null) {
            throw new AuthenException(ErrorCode.SERVER_NOT_RESPONSE);
        }
        return decodeAndDeserializeBytesResponse(responseMessage, responseClass);
    }

}
