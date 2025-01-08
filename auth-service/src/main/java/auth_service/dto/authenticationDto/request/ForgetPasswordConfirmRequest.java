package auth_service.dto.authenticationDto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgetPasswordConfirmRequest {
    @NotNull
    @Email
    String email;

    @NotNull
    int code;
    
}
