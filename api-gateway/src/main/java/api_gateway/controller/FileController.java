package api_gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import api_gateway.dto.fileDto.request.FileUploadRequest;
import api_gateway.dto.fileDto.response.FileUploadResponse;
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


    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Update account detail info"
    )
    public FileUploadResponse uploadFile(
            @Validated @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String name) throws IOException {

        FileUploadRequest request = FileUploadRequest.builder()
                .file(file).name(name).build();

//        FileUploadResponse response = fileService.uploadFile(request);

        return null;
    }

    //InService:

}
