package auth_service.dto.authenticationDto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate birthday;
    String nickName;
    String avatar;
    boolean isSubcribe;
}