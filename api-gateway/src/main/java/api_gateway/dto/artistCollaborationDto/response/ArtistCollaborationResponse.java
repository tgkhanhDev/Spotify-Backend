package api_gateway.dto.artistCollaborationDto.response;

//import music_service.dto.accountDto.response.AccountArtistResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import api_gateway.dto.authenticationDto.response.AccountArtistResponse;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ArtistCollaborationResponse {
    AccountArtistResponse accountId;
    String thumbnail;
}
