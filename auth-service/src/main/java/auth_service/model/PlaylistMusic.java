package auth_service.model;

import auth_service.model.embedKeys.PlaylistMusicKey;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "playlistmusic")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlaylistMusic {

    @EmbeddedId
    PlaylistMusicKey id;

    @Column(name = "addTime", nullable = false)
    LocalDateTime addTime;

    @ManyToOne
    @MapsId("playlistId") // Maps the playlistId from PlaylistMusicKey
    @JoinColumn(name = "playlistId", nullable = false)
    @JsonIgnore
    Playlist playlist;

    @ManyToOne
    @MapsId("musicId") // Maps the musicId from PlaylistMusicKey
    @JoinColumn(name = "musicId", nullable = false)
    Music music;

}
