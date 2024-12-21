package music_service.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "music")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Music {

    @Id
    UUID id;

    @Column(name = "musicName", nullable = false)
    String musicName;

    @Column(name = "uploadTime", nullable = false)
    LocalDate uploadTime;

    @Column(name = "thumbnail", nullable = false)
    String thumbnail;

    @OneToMany
    @JoinColumn(name = "musicId")
    List<ArtistCollaboration> artistCollaboration;
}
