package api_gateway.dto.musicDto.response;

import api_gateway.dto.artistCollaborationDto.response.ArtistCollaborationResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MusicResponse {

    UUID id;
    String musicName;
    List<ArtistCollaborationResponse> artistCollaboration;
    //DateAdd
    String uploadTime;
    String thumbnail; //ava
    String musicUrl;
}
