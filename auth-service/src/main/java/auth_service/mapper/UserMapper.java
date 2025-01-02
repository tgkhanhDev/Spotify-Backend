package auth_service.mapper;

import auth_service.dto.authenticationDto.response.UserResponse;
import auth_service.model.Account;
import auth_service.dto.authenticationDto.request.CreateUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    Account toUser(CreateUserRequest createUserRequest);
    UserResponse toUserResponse(Account user);
}
