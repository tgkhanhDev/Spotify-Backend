package music_service.model;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Document(indexName = "music")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MusicEls {

    @Id
    String id;

    @Field(name = "musicName", type = FieldType.Text)
    String musicName;

    @Field(name = "uploadTime", type = FieldType.Date)
    LocalDate uploadTime;

    @Field(name = "thumbnail", type = FieldType.Text)
    String thumbnail;

    @Field(type = FieldType.Nested)
    private List<ArtistCollaboration> artistcollaboration;


//    "artistcollaboration": [
//    {
//        "account": {
//                "nickname": "UserTwo",
//                "id": "dae9a8e4-26d4-4254-ae09-53366e1e207c"
//        },
//        "thumbnail": "collab2.jpg"
//    }
//    ],

//    private class ArtistCollaborationEls {
//        private thumbnail;
//    }
}


