package music_service.service;

import music_service.dto.accountDto.response.CheckMailResponse;
import music_service.dto.authenticationDto.request.AuthenticationRequest;
import music_service.dto.authenticationDto.request.CheckEmailRequest;
import music_service.dto.authenticationDto.request.CreateAccountRequest;
import music_service.dto.authenticationDto.response.AccountResponse;

public interface AccountService {
    CheckMailResponse checkMail(CheckEmailRequest email);

    AccountResponse registerAccount(CreateAccountRequest createAccountRequest);

}
