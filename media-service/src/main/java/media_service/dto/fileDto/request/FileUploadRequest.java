package media_service.dto.fileDto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import lombok.experimental.FieldDefaults;
import media_service.config.customDeserializer.FileUploadRequestDeserializer;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonDeserialize(using = FileUploadRequestDeserializer.class)
public class FileUploadRequest{
    MultipartFile file;
    String name;
}
