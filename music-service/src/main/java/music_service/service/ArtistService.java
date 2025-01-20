package music_service.service;

import music_service.dto.artistCollaborationDto.response.ArtistGeneralResponse;
import music_service.dto.authenticationDto.response.AccountResponse;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface ArtistService {
    List<ArtistGeneralResponse> findArtistByName(String name);

    AccountResponse becomeArtist (Jwt jwt);
}
