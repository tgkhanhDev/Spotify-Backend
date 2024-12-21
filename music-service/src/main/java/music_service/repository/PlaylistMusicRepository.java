package music_service.repository;

import music_service.model.PlaylistMusic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlaylistMusicRepository extends JpaRepository<PlaylistMusic, UUID> {

}
