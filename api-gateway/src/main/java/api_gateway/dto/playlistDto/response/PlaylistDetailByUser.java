package api_gateway.dto.playlistDto.response;

import api_gateway.dto.authenticationDto.response.AccountResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
