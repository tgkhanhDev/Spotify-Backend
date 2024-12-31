package api_gateway.dto.fileDto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileUploadResponse{
        String id;
        String name;
        String url;
        Long duration;
}