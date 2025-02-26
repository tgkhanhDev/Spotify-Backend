package music_service.repository;

import music_service.dto.musicDto.response.MusicResponse;
import music_service.model.Music;
import music_service.model.MusicEls;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface CustomMusicElsRepository {

    List<MusicEls> findWithFilter(String nickname);
    List<MusicEls> findByArtist(String artistId);


    void deleteMusicEls(String musicId);

    void saveMusicEls(MusicEls musicEls);
}
