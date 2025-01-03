package auth_service.service;

import auth_service.exception.AuthenException;
import auth_service.exception.ErrorCode;
import auth_service.mapper.AccountMapper;
import auth_service.model.Account;
import auth_service.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import auth_service.dto.accountDto.request.UpdateAccountRequest;
import auth_service.dto.accountDto.request.UpdateUserInfoRequest;
import auth_service.dto.accountDto.response.CheckMailResponse;
import auth_service.dto.authenticationDto.request.CheckEmailRequest;
import auth_service.dto.authenticationDto.request.CreateAccountRequest;
import auth_service.dto.authenticationDto.response.AccountResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

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

    @Override
    public AccountResponse updateUserInfo(UpdateUserInfoRequest request) {
        //Check validity
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenException(ErrorCode.UNAUTHORIZED);
        }

        // Get JWT token details
        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            UUID userIdClaims = UUID.fromString(jwt.getClaim("userId")); // Replace "sub" with the appropriate claim key for user ID

            //Update user info
            Account account = accountRepository.findById(userIdClaims).orElseThrow(() -> new AuthenException(ErrorCode.USER_NOT_EXISTED));
            LocalDate date = LocalDate.parse(request.getDateOfBirth(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            if(date.isAfter(LocalDate.now())) throw new AuthenException(ErrorCode.DATE_OF_BIRTH_INVALID);

            account.setBirthday(date);
//            account.setBirthday(request.getDateOfBirth());
            account.setGender(request.isGender());
            accountRepository.save(account);
            return accountMapper.toAccountResponse(account);

        } else {
            throw new AuthenException(ErrorCode.CLAIM_NOT_FOUND);
        }


    }

    @Override
    public AccountResponse updateAccountInfo(UpdateAccountRequest request) {
        //Check validity
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenException(ErrorCode.UNAUTHORIZED);
        }

        // Get JWT token details
        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            UUID userIdClaims = UUID.fromString(jwt.getClaim("userId")); // Replace "sub" with the appropriate claim key for user ID

            //Update user info
            Account account = accountRepository.findById(userIdClaims).orElseThrow(() -> new AuthenException(ErrorCode.USER_NOT_EXISTED));

            account.setNickName(request.getName());
            account.setAvatar(request.getAvatar());

            accountRepository.save(account);
            return accountMapper.toAccountResponse(account);

        } else {
            throw new AuthenException(ErrorCode.CLAIM_NOT_FOUND);
        }
    }


}
