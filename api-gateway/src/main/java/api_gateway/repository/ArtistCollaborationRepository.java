package api_gateway.repository;

import api_gateway.model.ArtistCollaboration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArtistCollaborationRepository extends JpaRepository<ArtistCollaboration, UUID> {

}
