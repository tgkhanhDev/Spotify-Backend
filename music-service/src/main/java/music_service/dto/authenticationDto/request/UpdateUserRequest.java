package music_service.dto.authenticationDto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    String password;
    String phoneNumber;
    String firstName;
    String lastName;
}
