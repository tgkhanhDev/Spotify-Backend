package api_gateway.dto.authenticationDto.response;

import api_gateway.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    @JsonProperty("user_id")
    UUID userId;
    String email;
    @JsonProperty("phone_number")
    String phoneNumber;
    @JsonProperty("first_name")
    String firstName;
    @JsonProperty("last_name")
    String lastName;
    UserRole role;
    @JsonProperty("is_active")
    boolean isActive;
}
