package auth_service.dto.accountDto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckMailResponse {
    boolean isExisted;
    String message;
}
