package music_service.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @GeneratedValue
    UUID id;

    @Column(name = "musicName", nullable = false)
    String musicName;

    @Column(name = "uploadTime", nullable = false)
    LocalDateTime uploadTime;

    @Column(name = "thumbnail", nullable = false)
    String thumbnail;

    @Column(name = "musicurl", nullable = false)
    String musicUrl;

    @OneToMany(mappedBy = "music", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ArtistCollaboration> artistCollaboration;
}
