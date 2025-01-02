package auth_service.dto.accountDto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserInfoRequest {
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Date of birth must be in the format DD/MM/YYYY")
    @JsonFormat(pattern = "dd/MM/yyyy")
    String dateOfBirth;
    boolean gender;  //isMale?
}
