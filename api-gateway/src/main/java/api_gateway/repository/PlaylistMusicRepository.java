package api_gateway.repository;

import api_gateway.model.PlaylistMusic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlaylistMusicRepository extends JpaRepository<PlaylistMusic, UUID> {

    @Query("SELECT pm FROM PlaylistMusic pm WHERE pm.id.musicId = :musicId AND pm.id.playlistId = :playlistId")
    Optional<PlaylistMusic> findByMusicIdAndPlaylistId(@Param("musicId") UUID musicId, @Param("playlistId") UUID playlistId);

    @Query("DELETE FROM PlaylistMusic pm WHERE pm.id.musicId = :musicId AND pm.id.playlistId = :playlistId")
    @Modifying
    void deleteByMusicIdAndPlaylistId(@Param("musicId") UUID musicId, @Param("playlistId") UUID playlistId);

}
