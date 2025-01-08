package api_gateway.dto.fileDto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MultipartFileCustom {
    MultipartFile file;
    String name;

    //request.getFile().getOriginalFilename
    //request.getFile().isEmpty
    //request.getFile().transferTo(File...)
}
