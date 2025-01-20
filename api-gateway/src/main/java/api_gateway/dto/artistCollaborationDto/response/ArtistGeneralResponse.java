package api_gateway.dto.artistCollaborationDto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ArtistGeneralResponse {
    UUID id;
    String nickName;
    String thumbnail;
}
