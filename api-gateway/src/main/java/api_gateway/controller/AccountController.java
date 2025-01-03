package api_gateway.controller;

import api_gateway.config.CustomMessageSender;
import api_gateway.dto.accountDto.request.UpdateAccountRequest;
import api_gateway.dto.accountDto.request.UpdateUserInfoRequest;
import api_gateway.dto.authenticationDto.response.AccountResponse;
import api_gateway.dto.authenticationDto.response.AuthenticationResponse;
import api_gateway.exception.AuthenException;
import api_gateway.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@Tag(name = "User")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class AccountController {
//    AuthenticationService authenticationService;
//    AccountService accountService;
//
//    @Autowired
//    public AccountController(AuthenticationService authenticationService, AccountService accountService) {
//        this.authenticationService = authenticationService;
//        this.accountService = accountService;
//    }
//
//    @PostMapping("/update")
//    @Operation(
//            summary = "*Update user detail info"
//    )
//    public AccountResponse updateUserInfo(@RequestBody @Valid UpdateUserInfoRequest request) {
//        return accountService.updateUserInfo(request);
//    }
//
//    @PostMapping("/updateAccount")
//    @Operation(
//            summary = "*Update account detail info"
//    )
//    public AccountResponse updateAccountInfo(@RequestBody @Valid UpdateAccountRequest request) {
//        return accountService.updateAccountInfo(request);
//    }

    @Value("${rabbitmq.exchange.name}")
    String exchange;
    @Value("${rabbitmq.user.queue.name}")
    String userQueue;
    private final RabbitTemplate rabbitTemplate;
    private final CustomMessageSender customMessageSender;

    @Autowired
    public AccountController(RabbitTemplate rabbitTemplate, CustomMessageSender customMessageSender) {
        this.rabbitTemplate = rabbitTemplate;
        this.customMessageSender = customMessageSender;
    }

    @PostMapping("/update")
    @Operation(
            summary = "*Update user detail info"
    )
    public AccountResponse updateUserInfo(
            @RequestBody @Valid UpdateUserInfoRequest request) {
        String jwtToken = extractJwtToken();
//        System.out.println("authorizationHeader: " + token);
        String correlationId = UUID.randomUUID().toString();
        String replyQueueName = rabbitTemplate.execute(channel -> channel.queueDeclare().getQueue());

        // Serialize payload to bytes
        byte[] payload = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            payload = objectMapper.writeValueAsBytes(request);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }

        // Send the request
        rabbitTemplate.convertAndSend(
                exchange,
                "user.update-info",
                payload,
                message -> {
                    message.getMessageProperties().setContentType("application/json");
                    message.getMessageProperties().setCorrelationId(correlationId);
                    message.getMessageProperties().setReplyTo(replyQueueName); // Dynamic reply queue
                    // Add JWT token to message headers
                    message.getMessageProperties().setHeader("Authorization",  jwtToken);
                    return message;
                }
        );

        byte[] responseMessage = (byte[]) rabbitTemplate.receiveAndConvert(replyQueueName, 5000); // Wait for up to 5 seconds
        AccountResponse accountResponse = customMessageSender.decodeAndDeserializeBytesResponse(responseMessage, AccountResponse.class);
        return accountResponse;
    }

    @PostMapping("/updateAccount")
    @Operation(
            summary = "*Update account detail info"
    )
    public AccountResponse updateAccountInfo(@RequestBody @Valid UpdateAccountRequest request) {
//        return accountService.updateAccountInfo(request);
        String jwtToken = extractJwtToken();
//        System.out.println("authorizationHeader: " + token);
        String correlationId = UUID.randomUUID().toString();
        String replyQueueName = rabbitTemplate.execute(channel -> channel.queueDeclare().getQueue());

        // Serialize payload to bytes
        byte[] payload = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            payload = objectMapper.writeValueAsBytes(request);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }

        // Send the request
        rabbitTemplate.convertAndSend(
                exchange,
                "user.update-account-detail",
                payload,
                message -> {
                    message.getMessageProperties().setContentType("application/json");
                    message.getMessageProperties().setCorrelationId(correlationId);
                    message.getMessageProperties().setReplyTo(replyQueueName); // Dynamic reply queue
                    // Add JWT token to message headers
                    message.getMessageProperties().setHeader("Authorization",  jwtToken);
                    return message;
                }
        );

        byte[] responseMessage = (byte[]) rabbitTemplate.receiveAndConvert(replyQueueName, 5000); // Wait for up to 5 seconds
        AccountResponse accountResponse = customMessageSender.decodeAndDeserializeBytesResponse(responseMessage, AccountResponse.class);
        return accountResponse;
    }

    private String extractJwtToken() {
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
