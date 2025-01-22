package api_gateway.dto.authenticationDto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountArtistResponse {
    @JsonProperty("id")
    UUID id;
    @JsonProperty("nickname")
    String nickName;
}
