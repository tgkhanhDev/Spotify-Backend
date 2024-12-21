package music_service.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error. Please report this to Khanh.", HttpStatus.INTERNAL_SERVER_ERROR),
    NULL_VALUE(1000, "Null value", HttpStatus.BAD_REQUEST),
    INVALID_KEY(1001, "Invalid key", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User already exists", HttpStatus.BAD_REQUEST),
    PHONE_EXISTED(1003, "Phone number already exists", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1004, "Username must be at least 3 characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1005, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1006, "User does not exist", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1007, "Unauthenticated user", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1008, "You do not have permission", HttpStatus.FORBIDDEN);

    int code;
    String message;
    HttpStatusCode statusCode;
}
