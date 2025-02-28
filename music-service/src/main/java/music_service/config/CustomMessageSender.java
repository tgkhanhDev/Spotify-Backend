package music_service.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import music_service.config.JWT.CustomJwtDecoder;
import music_service.exception.AuthenException;
import music_service.exception.ErrorCode;
import music_service.exception.ErrorCodeResponse;
import music_service.producer.AuthProducer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Component
@Slf4j
public class CustomMessageSender {

    private RabbitTemplate rabbitTemplate;
    @Autowired
    public CustomMessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


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
                        String jwtToken = getTokenFromContext();
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


    //Pass to header using jwt
    public <P, R> R customEventSenderWithJWT(String exchange, String routingKey, String jwtToken, P payload, Class<R> responseClass) {
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
                    if (jwtToken != null) {
                        String jwt = jwtToken;
                        message.getMessageProperties().setHeader("Authorization", jwt);
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

    //? This function only use for get Token from HTTP Request
    public String getTokenFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the authentication is valid
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenException(ErrorCode.UNAUTHORIZED);
        }

        // Extract the Jwt object from the credentials (or principal)
        Object credentials = authentication.getCredentials();

        // Ensure the credentials are of type Jwt
        if (credentials instanceof Jwt) {
            Jwt jwt = (Jwt) credentials;
            return jwt.getTokenValue();  // Get the raw JWT token as a string
        }

        // If the credentials aren't of type Jwt, throw an exception
        throw new AuthenException(ErrorCode.INVALID_TOKEN);
    }
}
