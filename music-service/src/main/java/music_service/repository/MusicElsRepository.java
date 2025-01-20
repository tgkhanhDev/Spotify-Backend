package music_service.repository;

import music_service.model.MusicEls;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicElsRepository extends ElasticsearchRepository<MusicEls, String> {

}
