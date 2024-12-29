package api_gateway.service;

import api_gateway.dto.accountDto.request.UpdateAccountRequest;
import api_gateway.dto.accountDto.request.UpdateUserInfoRequest;
import api_gateway.dto.accountDto.response.CheckMailResponse;
import api_gateway.dto.authenticationDto.request.CheckEmailRequest;
import api_gateway.dto.authenticationDto.request.CreateAccountRequest;
import api_gateway.dto.authenticationDto.response.AccountResponse;

public interface AccountService {
    CheckMailResponse checkMail(CheckEmailRequest email);

    AccountResponse registerAccount(CreateAccountRequest createAccountRequest);

    AccountResponse updateUserInfo(UpdateUserInfoRequest request);

    AccountResponse updateAccountInfo(UpdateAccountRequest request);

}
