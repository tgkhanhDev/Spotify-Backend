package api_gateway.model;


import api_gateway.model.embedKeys.RecentSongKey;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "recentsong")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecentSong {

    @EmbeddedId
    RecentSongKey id;

    @ManyToOne()
    @MapsId("accountId")
    @JoinColumn(name = "accountId", nullable = false)
    Account accountId;

    @ManyToOne()
    @MapsId("musicId")
    @JoinColumn(name = "musicId", nullable = false)
    Playlist musicId;

    @Column(name = "playtime", nullable = false)
    LocalDate playTime;

}
