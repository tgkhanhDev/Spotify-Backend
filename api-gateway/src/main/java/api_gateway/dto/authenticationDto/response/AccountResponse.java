package api_gateway.dto.authenticationDto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    LocalDate birthday;
    String nickName;
    String avatar;
    @JsonProperty("isSubcribe")
    boolean isSubcribe;
}
