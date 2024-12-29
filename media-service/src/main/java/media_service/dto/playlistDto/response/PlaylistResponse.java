package media_service.dto.playlistDto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import media_service.dto.playlistMusicDto.response.PlaylistMusicOnlyMusicResponse;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlaylistResponse {

    UUID playlistId;
    String backgroundImage;
    String title;
    int lengthOfTime;
    int songCount;
    //DateAdd
    String description;
    List<PlaylistMusicOnlyMusicResponse> musics;
}
