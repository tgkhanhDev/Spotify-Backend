package api_gateway.repository;

import api_gateway.model.Playlist;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {
    List<Playlist> findAllByAccount_Id(UUID creatorId);
    List<Playlist> findAllByAccount_Id(UUID creatorId, Sort sort);

    long countByAccount_Id(UUID creatorId);
}
