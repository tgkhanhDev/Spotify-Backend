package music_service.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import music_service.dto.accountDto.response.CheckMailResponse;
import music_service.dto.authenticationDto.request.AuthenticationRequest;
import music_service.dto.authenticationDto.request.CheckEmailRequest;
import music_service.dto.authenticationDto.request.CreateAccountRequest;
import music_service.dto.authenticationDto.response.AccountResponse;
import music_service.exception.AuthenException;
import music_service.exception.ErrorCode;
import music_service.mapper.AccountMapper;
import music_service.model.Account;
import music_service.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountServiceImpl implements AccountService {

    AuthenticationService authenticationService;
    AccountRepository accountRepository;
    AccountMapper accountMapper;

    public AccountServiceImpl(AuthenticationService authenticationService, AccountRepository accountRepository, AccountMapper accountMapper) {
        this.authenticationService = authenticationService;
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    public CheckMailResponse checkMail(CheckEmailRequest email) {
        boolean isMailExist = accountRepository.existsByEmail(email.getEmail());

        if(isMailExist) return new CheckMailResponse(true, ErrorCode.MAIL_EXISTED.getMessage());

        return new CheckMailResponse(isMailExist, "Email này phù hợp!");
    }

    @Override
    @Transactional
    public AccountResponse registerAccount(CreateAccountRequest createAccountRequest) {

        if(accountRepository.existsByEmail(createAccountRequest.getEmail())) throw new AuthenException(ErrorCode.MAIL_EXISTED);

        Account account = accountMapper.toAccount(createAccountRequest);
        account.setSubcribe(false);
        account.setAvatar("");
        account.setPassword(authenticationService.passwordEncoder().encode(createAccountRequest.getPassword()));
        Account accountSaved = accountRepository.save(account);

        return accountMapper.toAccountResponse(accountSaved);
    }


}
