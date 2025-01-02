package auth_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import auth_service.dto.accountDto.request.UpdateAccountRequest;
import auth_service.dto.accountDto.request.UpdateUserInfoRequest;
import auth_service.dto.authenticationDto.response.AccountResponse;
import auth_service.service.AccountService;
import auth_service.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Tag(name = "User")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountController {
    AuthenticationService authenticationService;
    AccountService accountService;

    @Autowired
    public AccountController(AuthenticationService authenticationService, AccountService accountService) {
        this.authenticationService = authenticationService;
        this.accountService = accountService;
    }

    @PostMapping("/update")
    @Operation(
            summary = "Update user detail info"
    )
    public AccountResponse updateUserInfo(@RequestBody @Valid UpdateUserInfoRequest request) {
        return accountService.updateUserInfo(request);
    }

    @PostMapping("/updateAccount")
    @Operation(
            summary = "Update account detail info"
    )
    public AccountResponse updateAccountInfo(@RequestBody @Valid UpdateAccountRequest request) {
        return accountService.updateAccountInfo(request);
    }

}
