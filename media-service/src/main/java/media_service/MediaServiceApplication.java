package media_service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "MediaService", version = "1.0", description = "IO File"))
public class MediaServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(MediaServiceApplication.class, args);
	}
}
