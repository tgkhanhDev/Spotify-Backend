package music_service.mapper;

import music_service.dto.accountDto.response.AccountArtistResponse;
import music_service.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {
    AccountArtistResponse toAccountUserMapperResponse(Account account);
}
