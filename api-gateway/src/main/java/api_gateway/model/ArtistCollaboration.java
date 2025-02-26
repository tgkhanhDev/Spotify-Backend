package api_gateway.model;


import api_gateway.model.embedKeys.ArtistCollabKey;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("account")
    Account accountId;

    @Column(name = "thumbnail", nullable = false)
    String thumbnail;


}
