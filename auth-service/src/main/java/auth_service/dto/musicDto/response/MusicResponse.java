package auth_service.dto.musicDto.response;

import auth_service.dto.artistCollaborationDto.response.ArtistCollaborationResponse;
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
}
