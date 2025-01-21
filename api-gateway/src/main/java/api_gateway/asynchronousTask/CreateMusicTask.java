package api_gateway.asynchronousTask;

import api_gateway.config.CustomMessageSender;
import api_gateway.dto.fileDto.request.FileUploadRequest;
import api_gateway.dto.fileDto.response.FileUploadResponse;
import api_gateway.dto.musicDto.request.MusicRequest;
import api_gateway.dto.musicDto.response.MusicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
public class CreateMusicTask {

    @Value("${rabbitmq.exchange.name}")
    String exchange;
    @Autowired
    CustomMessageSender customMessageSender;

    @Async
    public CompletableFuture<FileUploadResponse> uploadImageTask(MultipartFile thumbnail) throws InterruptedException {

        FileUploadRequest request = FileUploadRequest.builder()
                .file(thumbnail).build();
        String imageRoutingKey = "media.upload-file-image";
        return CompletableFuture.completedFuture(customMessageSender.customEventSender(exchange, imageRoutingKey, true, request, FileUploadResponse.class));
    }

    @Async
    public CompletableFuture<FileUploadResponse> uploadAudioTask(MultipartFile thumbnail, String musicName) throws InterruptedException {

        FileUploadRequest request = FileUploadRequest.builder()
                .file(thumbnail).name(musicName).build();
        String audioRoutingKey = "media.upload-file-audio";
        return CompletableFuture.completedFuture(customMessageSender.customEventSender(exchange, audioRoutingKey, true, request, FileUploadResponse.class));
    }


    public CompletableFuture<MusicResponse> addMusic(
            String routingKey,
            MultipartFile thumbnail,
            MultipartFile musicUrl,
            String musicName
    ) throws InterruptedException {

        return CompletableFuture.allOf(uploadImageTask(thumbnail), uploadAudioTask(musicUrl, musicName)) // Wait for both Task A and Task B to complete
                .thenApplyAsync(v -> {
                    try {
                        FileUploadResponse imageResponse = uploadImageTask(thumbnail).get();
                        System.out.println("UploadImageTask Completed");

                        FileUploadResponse audioResponse = uploadAudioTask(musicUrl, musicName).get();
                        System.out.println("AudioImageTask Completed");

                        // Combine results or process them as needed
                        System.out.println("Task C result with " + imageResponse.toString() + " and " + audioResponse.toString());
                        MusicRequest musicRequest = MusicRequest.builder()
                                .musicName(musicName)
                                .musicUrl(audioResponse.getUrl())
                                .thumbnail(imageResponse.getUrl())
                                .uploadTime(LocalDateTime.now().toString())
                                .build();

                        return customMessageSender.customEventSender(exchange, routingKey, true, musicRequest, MusicResponse.class);


                    } catch (Exception e) {
                        throw new RuntimeException("Error while processing Task C", e);
                    }
                });
    }

}
