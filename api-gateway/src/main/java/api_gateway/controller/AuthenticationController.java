package api_gateway.controller;

import api_gateway.service.AccountService;
import api_gateway.service.AuthenticationService;
import api_gateway.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import api_gateway.dto.accountDto.response.CheckMailResponse;
import api_gateway.dto.authenticationDto.request.AuthenticationRequest;
import api_gateway.dto.authenticationDto.request.CheckEmailRequest;
import api_gateway.dto.authenticationDto.request.CreateAccountRequest;
import api_gateway.dto.authenticationDto.request.ResetPasswordRequest;
import api_gateway.dto.authenticationDto.response.AccountResponse;
import api_gateway.dto.authenticationDto.response.AuthenticationResponse;
import api_gateway.dto.authenticationDto.response.EmailAuthenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "For Authentication")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    AccountService accountService;
    EmailService emailService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService, AccountService accountService, EmailService emailService) {
        this.authenticationService = authenticationService;
        this.accountService = accountService;
        this.emailService = emailService;
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
        return authenticationService.authenticate(authenticationRequest);
    }

    @PostMapping("/checkEmail")
    @Operation(summary = "Check Email")
    public CheckMailResponse checkEmail(@RequestBody() CheckEmailRequest email) {
        return accountService.checkMail(email);
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

        return accountService.registerAccount(createAccountRequest);
    }

    @PostMapping("/reset")
    @Operation(summary = "Reset password")
    public AccountResponse resetPassword(ResetPasswordRequest request) {
        return authenticationService.resetPassword(request);
    }

    @PostMapping("/forget")
    @Operation(summary = "Forget password (This action will send email to reset password)")
    public EmailAuthenResponse forgetPassword(@RequestParam String email) {
        CheckMailResponse checkMailResponse = accountService.checkMail(CheckEmailRequest.builder().email(email).build());

        if (!checkMailResponse.isExisted()) {
            return EmailAuthenResponse
                    .builder()
                    .code(404)
                    .message("Email Not Found")
                    .isValid(checkMailResponse.isExisted())
                    .build();
        }


        return EmailAuthenResponse
                .builder()
                .code(200)
                .message("Success")
                .isValid(emailService.sendEmail(email))
                .build();
    }

    @PostMapping("/forget-confirm")
    @Operation(summary = "Confirm code sent to email (Last for 5 minutes)")
    public EmailAuthenResponse forgetPasswordConfirm(@RequestParam String email, @RequestParam int code) {

        CheckMailResponse checkMailResponse = accountService.checkMail(CheckEmailRequest.builder().email(email).build());

        if (!checkMailResponse.isExisted()) {
            return EmailAuthenResponse
                    .builder()
                    .code(404)
                    .message("Email Not Found")
                    .isValid(checkMailResponse.isExisted())
                    .build();
        }

        return emailService.verifyCode(email, code);
    }
}
