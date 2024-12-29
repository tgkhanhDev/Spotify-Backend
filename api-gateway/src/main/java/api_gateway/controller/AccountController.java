package api_gateway.controller;

import api_gateway.service.AccountService;
import api_gateway.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import api_gateway.dto.accountDto.request.UpdateAccountRequest;
import api_gateway.dto.accountDto.request.UpdateUserInfoRequest;
import api_gateway.dto.authenticationDto.response.AccountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
