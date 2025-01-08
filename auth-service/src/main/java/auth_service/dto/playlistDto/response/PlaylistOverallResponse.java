package auth_service.dto.playlistDto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlaylistOverallResponse {
    UUID playlistId;
    String backgroundImage;
    String title;
}
