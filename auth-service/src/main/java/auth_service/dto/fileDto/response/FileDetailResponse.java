package auth_service.dto.fileDto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileDetailResponse{
        String name;
        LocalDateTime uploadTime;
        Long duration;
}
