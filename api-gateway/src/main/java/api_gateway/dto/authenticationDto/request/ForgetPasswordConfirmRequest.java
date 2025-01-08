package api_gateway.dto.authenticationDto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
