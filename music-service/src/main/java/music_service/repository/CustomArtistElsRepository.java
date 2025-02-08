package music_service.repository;

import music_service.model.ArtistEls;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CustomArtistElsRepository {
    List<ArtistEls> findByNicknameCustom(String nickname);
    List<ArtistEls> findAllArtist();
    ArtistEls save(ArtistEls artistEls) throws Exception;
}