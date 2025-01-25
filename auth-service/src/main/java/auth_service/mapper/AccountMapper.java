package auth_service.mapper;

import auth_service.dto.authenticationDto.response.AccountArtistResponse;
import auth_service.model.Account;
import auth_service.dto.authenticationDto.request.CreateAccountRequest;
import auth_service.dto.authenticationDto.response.AccountResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountArtistResponse toAccountUserMapperResponse(Account account);

    @Mappings({
            @Mapping(source = "name", target = "nickName"),
            @Mapping(source = "dateOfBirth", target = "birthday", dateFormat = "dd/MM/yyyy")
    })
    Account toAccount(CreateAccountRequest createAccountRequest);

    AccountResponse toAccountResponse(Account account);
}
