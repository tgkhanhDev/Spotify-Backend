package music_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
//import org.springframework.data.elasticsearch.annotations.Document;

import java.util.UUID;

//@Document(indexName = "artist-collaboration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ArtistEls {
    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("@timestamp")
    private String timestamp;

    @JsonProperty("id")
    private String id;

    @JsonProperty("@version")
    private String version;

    @JsonProperty("type")
    private String type;
}
