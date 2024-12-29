package music_service.mapper;

import music_service.dto.authenticationDto.request.CreateAccountRequest;
import music_service.dto.authenticationDto.response.AccountArtistResponse;
import music_service.dto.authenticationDto.response.AccountResponse;
import music_service.model.Account;
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
