package api_gateway.mapper;

import api_gateway.model.Account;
import api_gateway.dto.authenticationDto.request.CreateUserRequest;
import api_gateway.dto.authenticationDto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    Account toUser(CreateUserRequest createUserRequest);
    UserResponse toUserResponse(Account user);
}
