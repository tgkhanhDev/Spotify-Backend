package api_gateway.dto.musicDto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MusicRequest {
    String musicName;
    String uploadTime;
    String thumbnail;
    String musicUrl;
}



