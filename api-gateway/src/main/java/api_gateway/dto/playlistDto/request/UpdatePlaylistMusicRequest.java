package api_gateway.dto.playlistDto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePlaylistMusicRequest {
    @NotNull(message = "Playlist ID must not be null")
    UUID playlistId;

    @NotNull(message = "Music ID must not be null")
    UUID musicId;
}
