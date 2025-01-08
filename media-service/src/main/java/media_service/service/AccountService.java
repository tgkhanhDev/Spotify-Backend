package media_service.service;

import media_service.dto.accountDto.request.UpdateAccountRequest;
import media_service.dto.accountDto.request.UpdateUserInfoRequest;
import media_service.dto.accountDto.response.CheckMailResponse;
import media_service.dto.authenticationDto.request.CheckEmailRequest;
import media_service.dto.authenticationDto.request.CreateAccountRequest;
import media_service.dto.authenticationDto.response.AccountResponse;

public interface AccountService {
    CheckMailResponse checkMail(CheckEmailRequest email);

    AccountResponse registerAccount(CreateAccountRequest createAccountRequest);

    AccountResponse updateUserInfo(UpdateUserInfoRequest request);

    AccountResponse updateAccountInfo(UpdateAccountRequest request);

}
