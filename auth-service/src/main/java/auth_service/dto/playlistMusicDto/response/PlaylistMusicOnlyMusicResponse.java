package auth_service.dto.playlistMusicDto.response;

import auth_service.dto.artistCollaborationDto.response.ArtistCollaborationResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
