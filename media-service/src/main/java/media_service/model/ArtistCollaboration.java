package media_service.model;


import media_service.model.embedKeys.ArtistCollabKey;
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

    @ManyToOne()
    @MapsId("accountId")
    @JoinColumn(name = "accountId", nullable = false)
//    @JsonIgnore()
    Account accountId;

//    @ManyToOne()
//    @MapsId("musicId")
//    @JoinColumn(name = "musicId", nullable = false)
////    @JsonIgnore()
//    Music musicId;

    @Column(name = "thumbnail", nullable = false)
    String thumbnail;

}
