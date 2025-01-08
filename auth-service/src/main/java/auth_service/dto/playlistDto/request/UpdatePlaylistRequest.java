package auth_service.dto.playlistDto.request;

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
public class UpdatePlaylistRequest {
    @NotNull(message = "Playlist ID must not be null")
    UUID playlistId;

    @Size(max = 255, message = "Background image URL must not exceed 255 characters")
    String backgroundImage;

    @Size(max = 255, message = "Title must not exceed 100 characters")
    String title;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    String description;
}
