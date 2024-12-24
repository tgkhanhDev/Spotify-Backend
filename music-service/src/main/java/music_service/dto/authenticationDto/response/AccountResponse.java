package music_service.dto.authenticationDto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;
import music_service.enums.UserRole;

import java.time.LocalDate;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    UUID id;
    String email;
    boolean gender;
    LocalDate birthday;
    String nickName;
    String avatar;
    boolean isSubcribe;
}
