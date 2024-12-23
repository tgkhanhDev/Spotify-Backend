package music_service.repository;

import music_service.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {
    List<Playlist> findAllByAccount_Id(UUID creatorId);
    long countByAccount_Id(UUID creatorId);
}
