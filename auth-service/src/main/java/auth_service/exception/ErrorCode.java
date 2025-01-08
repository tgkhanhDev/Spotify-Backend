package auth_service.exception;

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
    INVALID_TOKEN(9997, "Token has expired or invalid", HttpStatus.UNAUTHORIZED),
    SERVER_NOT_RESPONSE(9998, "Server not response", HttpStatus.INTERNAL_SERVER_ERROR),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error. Please report this to Khanh.", HttpStatus.INTERNAL_SERVER_ERROR),
    NULL_VALUE(1000, "Null value", HttpStatus.BAD_REQUEST),
    INVALID_KEY(1001, "Invalid key", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User already exists", HttpStatus.BAD_REQUEST),
    PHONE_EXISTED(1003, "Phone number already exists", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1004, "Username must be at least 3 characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1005, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1006, "User does not exist", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1007, "Unauthenticated user", HttpStatus.UNAUTHORIZED),
    CLAIM_NOT_FOUND(1008, "Claim not found", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1008, "You do not have permission", HttpStatus.FORBIDDEN),
    USER_DOES_NOT_HAVE_PERMISSION(1008, "This user does not have permission", HttpStatus.FORBIDDEN),
    MAIL_EXISTED(1009, "Email này đã được Sử Dụng Bởi Starboy98", HttpStatus.BAD_REQUEST),
    DATE_FORMAT_INVALID(1010, "Date format invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH(1011, "Password does not match", HttpStatus.BAD_REQUEST),
    DATE_OF_BIRTH_INVALID(1012, "Are you a time traveller?", HttpStatus.BAD_REQUEST),
    CAN_NOT_DESERIALIZE(1013, "Can not deserialize", HttpStatus.BAD_REQUEST),
    INVALID_MESSAGE_QUEUE_REQUEST(1014, "Invalid message queue request", HttpStatus.BAD_REQUEST),
    //Music
    MUSIC_NOT_FOUND(2000, "Music not found", HttpStatus.NOT_FOUND),
    INVALID_MUSIC_FILE(2001, "System only support .mp3 file, please contact the administrator", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_FILE(2001, "Image file is invalid", HttpStatus.BAD_REQUEST),
    ERROR_WHEN_UPLOAD(2001, "An error occurred while processing the audio file", HttpStatus.BAD_REQUEST),
    MUSIC_ALREADY_IN_PLAYLIST(2001, "Music already in playlist", HttpStatus.BAD_REQUEST),
    MUSIC_NOT_FOUND_IN_PLAYLIST(2001, "Music not found in playlist", HttpStatus.BAD_REQUEST),
    PLAYLIST_NOT_FOUND(2000, "Playlist not found", HttpStatus.NOT_FOUND);


    int code;
    String message;
    HttpStatusCode statusCode;
}
