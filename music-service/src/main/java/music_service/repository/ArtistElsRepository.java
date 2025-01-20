package music_service.repository;

import music_service.model.ArtistEls;
import music_service.model.MusicEls;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistElsRepository extends ElasticsearchRepository<ArtistEls, String> {
    List<ArtistEls> findByNicknameLike(String nickname);
}
