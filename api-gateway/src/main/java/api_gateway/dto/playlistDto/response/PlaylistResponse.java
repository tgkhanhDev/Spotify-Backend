package api_gateway.dto.playlistDto.response;

import api_gateway.dto.playlistMusicDto.response.PlaylistMusicOnlyMusicResponse;
import api_gateway.model.Account;
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
    int lengthOfTime;
    int songCount;
    //DateAdd
    String description;
    Account account;
    List<PlaylistMusicOnlyMusicResponse> musics;
}
