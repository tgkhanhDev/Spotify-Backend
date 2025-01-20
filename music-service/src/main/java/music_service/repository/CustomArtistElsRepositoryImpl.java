package music_service.repository;

import music_service.model.ArtistEls;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomArtistElsRepositoryImpl implements CustomArtistElsRepository {

    @Override
    public List<ArtistEls> findByNicknameCustom(String nickname) {
        return null;
    }
}
