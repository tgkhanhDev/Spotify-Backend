package media_service.config.customDeserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import media_service.dto.fileDto.request.CustomMultipartFile;
import media_service.dto.fileDto.request.FileUploadRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Base64;

@Component
public class FileUploadRequestDeserializer extends JsonDeserializer<FileUploadRequest> {

    @Override
    public FileUploadRequest deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        ObjectCodec codec = p.getCodec();
        JsonNode node = codec.readTree(p);

        // Deserialize 'name'
        String name = node.get("name").asText();

        // Deserialize 'file'
        JsonNode fileNode = node.get("file");
        MultipartFile file = null;
        if (fileNode != null) {
            String fileName = fileNode.get("fileName").asText();
            String contentType = fileNode.get("contentType").asText();
            byte[] content = Base64.getDecoder().decode(fileNode.get("content").asText());
            file = new CustomMultipartFile(fileName, fileName, contentType, content);
        }

        return new FileUploadRequest(file, name);
    }
}
