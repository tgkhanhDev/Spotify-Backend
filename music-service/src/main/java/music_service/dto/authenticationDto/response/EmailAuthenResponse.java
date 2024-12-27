package music_service.dto.authenticationDto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailAuthenResponse {
    boolean isValid;
    int code;
    String message;
}
