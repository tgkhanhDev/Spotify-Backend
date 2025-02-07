package music_service.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import music_service.model.embedKeys.ArtistCollabKey;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "artistCollaboration")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ArtistCollaboration {

    @EmbeddedId
    ArtistCollabKey id;

    @ManyToOne
    @MapsId("musicId")  // Ensure it maps to the composite key
    @JoinColumn(name = "musicId", nullable = false)
    Music music;  // ✅ Reference to Music

    @ManyToOne
    @MapsId("accountId")
    @JoinColumn(name = "accountId", nullable = false)
    @JsonProperty("account")
    Account accountId;

    @Column(name = "thumbnail")
    String thumbnail;

}
