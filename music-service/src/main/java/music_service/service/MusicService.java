package music_service.service;

import music_service.dto.musicDto.request.MusicRequest;
import music_service.dto.musicDto.response.MusicResponse;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface MusicService {
    List<MusicResponse> getAllMusic(Jwt jwtToken, String searchText);
    List<MusicResponse> getAllMusicByArtist(String artistId);

    MusicResponse deleteMusic(Jwt jwtToken, String musicId);

    MusicResponse addMusic(Jwt jwtToken, MusicRequest musicRequest);

    List<MusicResponse> generateMusicQueueByMusicId(String musicQueueId);
}
