package auth_service.exception;

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
public class ErrorCodeResponse {
    ErrorCode errorCode;
}
