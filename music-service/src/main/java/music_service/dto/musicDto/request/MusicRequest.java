package music_service.dto.musicDto.request;
import org.springframework.web.multipart.MultipartFile;
import lombok.*;
import lombok.experimental.FieldDefaults;

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



