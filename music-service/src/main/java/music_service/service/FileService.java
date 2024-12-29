package music_service.service;

import music_service.dto.fileDto.request.FileUploadRequest;
import music_service.dto.fileDto.response.FileUploadResponse;

public interface FileService {
    FileUploadResponse uploadFile(FileUploadRequest request);
}
