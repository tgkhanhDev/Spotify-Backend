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
        String routingKey = "user.update-info";
        return customMessageSender.customEventSender(exchange, routingKey, true, request, AccountResponse.class);
    }

    @PostMapping("/updateAccount")
    @Operation(
            summary = "*Update account detail info"
    )
    public AccountResponse updateAccountInfo(@RequestBody @Valid UpdateAccountRequest request) {
        String routingKey = "user.update-account-detail";
        return customMessageSender.customEventSender(exchange, routingKey, true, request, AccountResponse.class);
    }

    @GetMapping("")
    @Operation(
            summary = "*Get user info"
    )
    public AccountResponse getUserInfo() {
        String routingKey = "user.get-user-info";
        return customMessageSender.customEventSender(exchange, routingKey, true, "", AccountResponse.class);
    }

}
