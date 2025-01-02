package auth_service.model;


import auth_service.model.embedKeys.SavedPlaylistKey;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "savedplaylist")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SavedPlaylist {

    @EmbeddedId
    SavedPlaylistKey id;
    @ManyToOne()
    @MapsId("accountId")
    @JoinColumn(name = "accountId", nullable = false)
    Account accountId;

    @ManyToOne()
    @MapsId("playlistId")
    @JoinColumn(name = "playlistId", nullable = false)
    Playlist playlistId;

    LocalDate savedTime;

}
