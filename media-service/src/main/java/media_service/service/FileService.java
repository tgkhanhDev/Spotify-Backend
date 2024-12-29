package media_service.service;

import media_service.dto.fileDto.request.FileUploadRequest;
import media_service.dto.fileDto.response.FileUploadResponse;

public interface FileService {
    FileUploadResponse uploadFile(FileUploadRequest request);
}
