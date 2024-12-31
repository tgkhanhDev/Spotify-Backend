package media_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import media_service.dto.fileDto.request.FileUploadRequest;
import media_service.dto.fileDto.response.FileUploadResponse;
import media_service.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/v1/file")
@Tag(name = "File")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileController {
    FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "/audio", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Update account detail info"
    )
    public FileUploadResponse uploadFileAudio(
            @Validated @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String name) throws IOException {

        FileUploadRequest request = FileUploadRequest.builder()
                .file(file).name(name).build();

        FileUploadResponse response = fileService.uploadFileAudio(request);

        return response;
    }

    @PostMapping(value = "/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Update account detail info"
    )
    public FileUploadResponse uploadFileImage(
            @Validated @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String name) throws IOException {

        FileUploadRequest request = FileUploadRequest.builder()
                .file(file).name(name).build();

        FileUploadResponse response = fileService.uploadFileImage(request);
        return response;
    }

    //InService:

}
