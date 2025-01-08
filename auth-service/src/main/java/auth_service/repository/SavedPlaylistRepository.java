package auth_service.repository;

import auth_service.model.SavedPlaylist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SavedPlaylistRepository extends JpaRepository<SavedPlaylist, UUID> {

}
