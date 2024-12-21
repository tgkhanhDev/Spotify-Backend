package music_service.dto.request;

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
public class CreateUserRequest {
    @NotNull
    @Email
    String email;

    @NotNull
    String phoneNumber;

    @NotNull
    String firstName;

    @NotNull
    String lastName;

    @NotNull
    @Size(min = 5, message = "Mật khẩu phải có ít nhất 5 ký tự")
    String password;
}
