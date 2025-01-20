package music_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;
import music_service.dto.artistCollaborationDto.response.ArtistCollaborationResponse;
import org.springframework.data.annotation.Id;
//import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.Field;
//import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

//@Document(indexName = "music")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MusicEls {

    @Id
    String id;

    @JsonProperty("musicname")
    String musicName;

    @JsonProperty("uploadtime")
    String uploadTime;

    @JsonProperty("thumbnail")
    String thumbnail;

    @JsonProperty("artistcollaboration")
    private List<ArtistCollaborationResponse> artistcollaboration;


    @JsonProperty("@timestamp")
    private String timestamp;
    @JsonProperty("@version")
    private String version;
    @JsonProperty("type")
    private String type;
}



