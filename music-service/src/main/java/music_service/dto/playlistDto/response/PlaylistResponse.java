package music_service.dto.playlistDto.response;

import music_service.dto.musicDto.response.MusicResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import music_service.dto.playlistMusicDto.response.PlaylistMusicOnlyMusicResponse;
import music_service.model.Account;
import music_service.model.PlaylistMusic;

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
    Account account;

    List<PlaylistMusicOnlyMusicResponse> musics;
}
