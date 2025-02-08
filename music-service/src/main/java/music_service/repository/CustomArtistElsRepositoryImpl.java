package music_service.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import lombok.extern.slf4j.Slf4j;
import music_service.model.ArtistEls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class CustomArtistElsRepositoryImpl implements CustomArtistElsRepository {

    @Autowired
    private ElasticsearchClient esClient;

    @Override
    public List<ArtistEls> findByNicknameCustom(String nickname) {
        try {
            SearchResponse<ArtistEls> response = esClient.search(s -> s
                            .index("artist-collaboration")
                            .size(5)
                            .query(q -> q
                                    .bool(b -> b
                                            .should(
                                                    should1 -> should1
                                                            .match(t -> t
                                                                    .field("nickname")
                                                                    .query(nickname)
                                                            )
                                            )
                                            .should(
                                                    should2 -> should2
                                                            .regexp(regex -> regex
                                                                    .field("nickname")
                                                                    .value(".*" + nickname + ".*")
                                                            )
                                            )
                                    )
                            ),
                    ArtistEls.class
            );
            TotalHits total = response.hits().total();
            boolean isExactResult = total.relation() == TotalHitsRelation.Eq;
//            !Debug
//            Logger logger = log;
//            if (isExactResult) {
//                logger.info("There are " + total.value() + " results");
//            } else {
//                logger.info("There are more than " + total.value() + " results");
//            }

            List<Hit<ArtistEls>> hits = response.hits().hits();

//            !Debug
//            for (Hit<ArtistEls> hit: hits) {
//                ArtistEls product = hit.source();
//                logger.info("Found product " + product.getNickname() + ", score " + hit.score());
//            }

            return hits.stream().map(Hit::source).toList();

        } catch (Exception e) {
            log.error("Error", e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ArtistEls> findAllArtist() {
        try {
            return esClient.search(s -> s
                            .index("artist-collaboration"),
                    ArtistEls.class
            ).hits().hits().stream().map(Hit::source).toList();
        } catch (Exception e) {
            log.error("Error", e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArtistEls save(ArtistEls artistEls) throws Exception {
        try {
            esClient.create(c -> c
                    .index("artist-collaboration")
                    .id(artistEls.getId())
                    .document(artistEls)
            );
            return artistEls;
        } catch (Exception e) {
            log.error("Error", e);
            e.printStackTrace();
            throw new Exception("Error: " + e.getMessage());
        }
    }
}
