package media_service.mapper;

import media_service.dto.authenticationDto.request.CreateUserRequest;
import media_service.dto.authenticationDto.response.UserResponse;
import media_service.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    Account toUser(CreateUserRequest createUserRequest);
    UserResponse toUserResponse(Account user);
}
