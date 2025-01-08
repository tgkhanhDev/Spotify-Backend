package media_service.service;

import media_service.dto.fileDto.request.FileUploadRequest;
import media_service.dto.fileDto.response.FileUploadResponse;
import org.springframework.security.oauth2.jwt.Jwt;

public interface FileService {
    FileUploadResponse uploadFileAudio(FileUploadRequest request, Jwt jwtToken);

    FileUploadResponse uploadFileImage(FileUploadRequest request, Jwt jwtToken);

}
