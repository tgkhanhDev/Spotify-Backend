package music_service.dto.playlistDto.response;

import music_service.dto.musicDto.response.MusicResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    //DateAdd
    String description;
    List<MusicResponse> musics;
}
