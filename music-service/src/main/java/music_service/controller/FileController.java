package music_service.controller;

import com.mpatric.mp3agic.Mp3File;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import music_service.dto.fileDto.request.FileUploadRequest;
import music_service.dto.fileDto.response.FileDetailResponse;
import music_service.dto.fileDto.response.FileUploadResponse;
import music_service.exception.ErrorCode;
import music_service.exception.MusicException;
import music_service.service.FileService;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.parser.Parser;
import org.apache.tika.parser.AutoDetectParser;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Update account detail info"
    )
    public FileUploadResponse uploadFile(
            @Validated @RequestParam("file") MultipartFile file,
            @RequestParam(value = "name", required = false) String name) throws IOException {

        FileUploadRequest request = FileUploadRequest.builder()
                .file(file).name(name).build();

        FileUploadResponse response = fileService.uploadFile(request);

        return response;
    }

    //InService:

}
