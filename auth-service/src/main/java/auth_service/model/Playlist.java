package auth_service.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "playlist")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Playlist {

    @Id
    @UuidGenerator
    UUID id;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "backgroundImage", nullable = true)
    String backgroundImage;

    @Column(name = "description", nullable = true)
    String description;

    @Column(name = "createdTime", nullable = false)
    LocalDateTime createdTime;

    @ManyToOne()
    @JoinColumn(name = "creatorId")
    Account account;

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    List<PlaylistMusic> playlistMusicSet;

}
