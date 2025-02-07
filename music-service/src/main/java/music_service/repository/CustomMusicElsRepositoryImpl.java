package music_service.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import lombok.extern.slf4j.Slf4j;
import music_service.model.ArtistEls;
import music_service.model.Music;
import music_service.model.MusicEls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
public class CustomMusicElsRepositoryImpl implements CustomMusicElsRepository {

    @Autowired
    private ElasticsearchClient esClient;

    @Override
    public List<MusicEls> findWithFilter(String searchText) {
        try {
            SearchResponse<MusicEls> response = esClient.search(s -> s
                            .index("music")
                            .size(5)
                            .query(q -> q
                                    .bool(b -> b
                                            .should(
                                                    should1 -> should1
                                                            .multiMatch(mm -> mm
                                                                    .query(searchText)
                                                                    .fields("musicname", "artistcollaboration.account.nickname")
                                                            )
                                            )
                                            .should(
                                                    should2 -> should2
                                                            .regexp(regex -> regex
                                                                    .field("musicname")
                                                                    .value(".*" + searchText + ".*")
                                                            )
                                            )
                                    )
                            ),
                    MusicEls.class
            );
            TotalHits total = response.hits().total();
            boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

            if (isExactResult) {
                log.info("There are " + total.value() + " results");
            } else {
                log.info("There are more than " + total.value() + " results");
            }

            List<Hit<MusicEls>> hits = response.hits().hits();
            return hits.stream().map(Hit::source).toList();

        } catch (Exception e) {
            log.error("Error", e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<MusicEls> findByArtist(String artistId) {
        try {
            SearchResponse<MusicEls> response = esClient.search(s -> s
                            .index("music")
                            .size(5)
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m
                                                    .term(t -> t
                                                            .field("artistcollaboration.account.id.keyword")
                                                            .value(artistId))
                                            )
                                    )
                            ),
                    MusicEls.class
            );
            TotalHits total = response.hits().total();
            boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

            if (isExactResult) {
                log.info("There are " + total.value() + " results");
            } else {
                log.info("There are more than " + total.value() + " results");
            }

            List<Hit<MusicEls>> hits = response.hits().hits();
            return hits.stream().map(Hit::source).toList();

        } catch (Exception e) {
            log.error("Error", e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteMusicEls(String musicId) {
        try {
            esClient.delete(i -> i
                    .index("music")
                    .id(musicId)
            );
        } catch (Exception e) {
            log.error("Error", e);
            e.printStackTrace();
        }
    }

    @Override
    public void saveMusicEls(MusicEls musicEls) {
        try {
            esClient.create(i -> i
                    .index("music")
                    .document(musicEls)
                    .id(musicEls.getId())
            );
        } catch (Exception e) {
            log.error("Error", e);
            e.printStackTrace();
        }
    }
}
