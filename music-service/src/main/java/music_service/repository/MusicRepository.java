package music_service.repository;

import music_service.model.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MusicRepository extends JpaRepository<Music, UUID> {
    @Query(value = "SELECT * FROM music ORDER BY RANDOM() LIMIT 20", nativeQuery = true)
    List<Music> findRandom20MusicQueue();
}
