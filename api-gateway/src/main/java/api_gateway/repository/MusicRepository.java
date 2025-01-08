package api_gateway.repository;

import api_gateway.model.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MusicRepository extends JpaRepository<Music, UUID> {

}
