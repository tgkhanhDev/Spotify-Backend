package api_gateway.service;

import api_gateway.dto.fileDto.request.FileUploadRequest;
import api_gateway.dto.fileDto.response.FileUploadResponse;

public interface FileService {
    FileUploadResponse uploadFile(FileUploadRequest request);
}
