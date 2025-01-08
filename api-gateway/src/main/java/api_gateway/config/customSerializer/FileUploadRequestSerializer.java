package api_gateway.config.customSerializer;

import api_gateway.dto.fileDto.request.FileUploadRequest;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Component
public class FileUploadRequestSerializer extends JsonSerializer<FileUploadRequest> {

    @Override
    public void serialize(FileUploadRequest value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        // Serialize the 'name' field
        gen.writeStringField("name", value.getName());

        // Serialize the 'file' field
        MultipartFile file = value.getFile();
        if (file != null) {
            gen.writeObjectFieldStart("file");
            gen.writeStringField("fileName", file.getOriginalFilename());
            gen.writeStringField("contentType", file.getContentType());
            gen.writeNumberField("size", file.getSize());
            gen.writeStringField("content", Base64.getEncoder().encodeToString(file.getBytes())); // Base64 encode the file content
            gen.writeEndObject();
        }
        gen.writeEndObject();
    }
    
}
