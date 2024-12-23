package music_service.dto.playlistDto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import music_service.dto.musicDto.response.MusicResponse;

import java.util.List;
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
