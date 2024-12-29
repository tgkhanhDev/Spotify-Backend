package media_service.dto.playlistMusicDto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import media_service.dto.artistCollaborationDto.response.ArtistCollaborationResponse;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlaylistMusicOnlyMusicResponse {

    UUID id;
    String musicName;
    String uploadTime;
    String thumbnail;
    List<ArtistCollaborationResponse> artistCollaboration;
}
