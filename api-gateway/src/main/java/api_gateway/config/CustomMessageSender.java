package api_gateway.config;

import api_gateway.dto.accountDto.response.CheckMailResponse;
import api_gateway.dto.authenticationDto.response.ApiResponse;
import api_gateway.exception.AuthenException;
import api_gateway.exception.ErrorCode;
import api_gateway.exception.ErrorCodeResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Slf4j
public class CustomMessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

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
        byte[] res = new ObjectMapper().writeValueAsBytes(data);

        // Step 2: Send the message using RabbitTemplate
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

}
