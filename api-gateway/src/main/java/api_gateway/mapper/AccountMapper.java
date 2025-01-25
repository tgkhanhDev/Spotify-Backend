package api_gateway.mapper;

import api_gateway.model.Account;
import api_gateway.dto.authenticationDto.request.CreateAccountRequest;
import api_gateway.dto.authenticationDto.response.AccountArtistResponse;
import api_gateway.dto.authenticationDto.response.AccountResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {
    AccountArtistResponse toAccountUserMapperResponse(Account account);

    @Mappings({
            @Mapping(source = "name", target = "nickName"),
            @Mapping(source = "dateOfBirth", target = "birthday", dateFormat = "dd/MM/yyyy")
    })
    Account toAccount(CreateAccountRequest createAccountRequest);

    AccountResponse toAccountResponse(Account account);
}
