package api_gateway.controller;

import api_gateway.config.CustomMessageSender;
import api_gateway.dto.accountDto.response.CheckMailResponse;
import api_gateway.dto.authenticationDto.request.*;
import api_gateway.dto.authenticationDto.response.AccountResponse;
import api_gateway.dto.authenticationDto.response.AuthenticationResponse;
import api_gateway.dto.authenticationDto.response.EmailAuthenResponse;
import api_gateway.exception.AuthenException;
import api_gateway.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "For Authentication")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class AuthenticationController {
    @Value("${rabbitmq.exchange.name}")
    String exchange;
    @Value("${rabbitmq.auth.queue.name}")
    String authQueue;
    private final RabbitTemplate rabbitTemplate;
    private final CustomMessageSender customMessageSender;

    @Autowired
    public AuthenticationController(RabbitTemplate rabbitTemplate, CustomMessageSender customMessageSender) {
        this.rabbitTemplate = rabbitTemplate;
        this.customMessageSender = customMessageSender;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login User",
            description = "Use the example credentials to test the login functionality.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login credentials",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Login Example",
                                    value = """
                                            {
                                              "email": "user1@example.com",
                                              "password": "123123"
                                            }
                                            """
                            )
                    )
            )
    )
    public AuthenticationResponse login(
            @RequestBody AuthenticationRequest authenticationRequest) {
        String routingKey = "auth.login";
        return customMessageSender.customEventSender(exchange, routingKey, false, authenticationRequest, AuthenticationResponse.class);
    }


    @PostMapping("/checkEmail")
    @Operation(summary = "Check Email")
    public CheckMailResponse checkEmail(@RequestBody() CheckEmailRequest emailRequest) {
        String routingKey = "auth.check-email";
        return customMessageSender.customEventSender(exchange, routingKey, false, emailRequest, CheckMailResponse.class);
    }

    @PostMapping("/signup")
    @Operation(
            summary = "Register new user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Login Example",
                                    value = """
                                            {
                                               "email": "user3@gmail.com",
                                               "password": "123123",
                                               "name": "Dung Duoc",
                                               "dateOfBirth": "27/11/2000",
                                               "gender": true
                                             }
                                            """
                            )
                    )
            )
    )
    public AccountResponse register(@RequestBody CreateAccountRequest createAccountRequest) {
        String routingKey = "auth.signup";
        return customMessageSender.customEventSender(exchange, routingKey, false, createAccountRequest, AccountResponse.class);
    }

    @PostMapping("/reset")
    @Operation(summary = "Reset password")
    public AccountResponse resetPassword(ResetPasswordRequest request) {
        String routingKey = "auth.reset-password";
        return customMessageSender.customEventSender(exchange, routingKey, false, request, AccountResponse.class);
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password")
    public AccountResponse resetPassword(ChangePasswordRequest request) {
        String routingKey = "auth.change-password";
        return customMessageSender.customEventSender(exchange, routingKey, false, request, AccountResponse.class);
    }

    @PostMapping("/forget")
    @Operation(summary = "Forget password (This action will send email to reset password)")
    public EmailAuthenResponse forgetPassword(@RequestParam String email) {
        CheckEmailRequest request = CheckEmailRequest.builder().email(email).build();
        String routingKey = "auth.forget-password";
        return customMessageSender.customEventSender(exchange, routingKey, false, request, EmailAuthenResponse.class);
    }

    @PostMapping("/forget-confirm")
    @Operation(summary = "Confirm code sent to email (Last for 5 minutes)")
    public EmailAuthenResponse forgetPasswordConfirm(@RequestParam String email, @RequestParam int code) {
        ForgetPasswordConfirmRequest request = ForgetPasswordConfirmRequest.builder().email(email).code(code).build();
        String routingKey = "auth.forget-password-confirm";
        return customMessageSender.customEventSender(exchange, routingKey, false, request, EmailAuthenResponse.class);
    }
}
