package music_service.dto.playlistMusicDto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import music_service.dto.artistCollaborationDto.response.ArtistCollaborationResponse;
import music_service.model.ArtistCollaboration;

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
