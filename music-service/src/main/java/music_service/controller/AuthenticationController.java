package music_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import music_service.dto.accountDto.response.CheckMailResponse;
import music_service.dto.authenticationDto.request.AuthenticationRequest;
import music_service.dto.authenticationDto.request.CheckEmailRequest;
import music_service.dto.authenticationDto.request.CreateAccountRequest;
import music_service.dto.authenticationDto.request.ResetPasswordRequest;
import music_service.dto.authenticationDto.response.AccountResponse;
import music_service.dto.authenticationDto.response.AuthenticationResponse;
import music_service.service.AccountService;
import music_service.service.AuthenticationService;
import music_service.service.EmailService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "For Authentication")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    AccountService accountService;

    public AuthenticationController(AuthenticationService authenticationService, AccountService accountService) {
        this.authenticationService = authenticationService;
        this.accountService = accountService;
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
//        emailService.sendEmail(request.getEmail());
        return authenticationService.resetPassword(request);
    }
}
