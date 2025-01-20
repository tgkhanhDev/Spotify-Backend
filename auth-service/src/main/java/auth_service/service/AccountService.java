package auth_service.service;

import auth_service.dto.accountDto.request.UpdateAccountRequest;
import auth_service.dto.accountDto.request.UpdateUserInfoRequest;
import auth_service.dto.accountDto.response.CheckMailResponse;
import auth_service.dto.authenticationDto.request.CheckEmailRequest;
import auth_service.dto.authenticationDto.request.CreateAccountRequest;
import auth_service.dto.authenticationDto.response.AccountResponse;
import org.springframework.security.oauth2.jwt.Jwt;

public interface AccountService {
    CheckMailResponse checkMail(CheckEmailRequest email);

    AccountResponse registerAccount(CreateAccountRequest createAccountRequest);

    AccountResponse updateUserInfo(UpdateUserInfoRequest request, Jwt JwtToken);

    AccountResponse updateAccountInfo(UpdateAccountRequest request, Jwt JwtToken);

    AccountResponse getUserInfo(Jwt JwtToken);

}
