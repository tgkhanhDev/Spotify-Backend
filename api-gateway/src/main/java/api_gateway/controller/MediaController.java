package api_gateway.controller;

import api_gateway.config.CustomMessageSender;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import api_gateway.dto.fileDto.request.FileUploadRequest;
import api_gateway.dto.fileDto.response.FileUploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/v1/file")
@Tag(name = "File")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MediaController {
    @Value("${rabbitmq.exchange.name}")
    String exchange;
    CustomMessageSender customMessageSender;

    @Autowired
    public MediaController(CustomMessageSender customMessageSender) {
        this.customMessageSender = customMessageSender;
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
        String routingKey = "media.upload-file-audio";
        return customMessageSender.customEventSender(exchange, routingKey, true, request, FileUploadResponse.class);

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
        String routingKey = "media.upload-file-image";
        return customMessageSender.customEventSender(exchange, routingKey, true, request, FileUploadResponse.class);
    }

}
