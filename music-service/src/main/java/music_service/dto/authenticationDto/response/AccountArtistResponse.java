package music_service.dto.authenticationDto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountArtistResponse {
    UUID id;
    String nickName;
}
