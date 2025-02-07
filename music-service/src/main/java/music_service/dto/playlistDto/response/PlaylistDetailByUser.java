package music_service.dto.playlistDto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import music_service.dto.authenticationDto.response.AccountResponse;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlaylistDetailByUser {
    AccountResponse account;
    List<PlaylistOverallResponse> playlists;
}
