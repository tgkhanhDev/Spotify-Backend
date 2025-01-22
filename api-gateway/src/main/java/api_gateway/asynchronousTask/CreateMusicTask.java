package api_gateway.asynchronousTask;

import api_gateway.config.CustomMessageSender;
import api_gateway.dto.fileDto.request.FileUploadRequest;
import api_gateway.dto.fileDto.response.FileUploadResponse;
import api_gateway.dto.musicDto.request.MusicRequest;
import api_gateway.dto.musicDto.response.MusicResponse;
import api_gateway.exception.AuthenException;
import api_gateway.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
@Slf4j
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("audioToken==========:" + authentication);
        return CompletableFuture.completedFuture(customMessageSender.customEventSender(exchange, audioRoutingKey, true, request, FileUploadResponse.class));
    }

    public CompletableFuture<MusicResponse> addMusic(
            String routingKey,
            MultipartFile thumbnail,
            MultipartFile musicUrl,
            String musicName
    ) {
        try {


            //Capture Context from ThreadLocal
            SecurityContext securityContext = SecurityContextHolder.getContext();

            // Start the asynchronous tasks
            CompletableFuture<FileUploadResponse> imageFuture = uploadImageTask(thumbnail);
            CompletableFuture<FileUploadResponse> audioFuture = uploadAudioTask(musicUrl, musicName);

            return CompletableFuture.allOf(imageFuture, audioFuture)
                    .thenApplyAsync(v -> {
                        try {

                            //Pass from ThreadLocal to Another Thread.
                            SecurityContextHolder.setContext(securityContext);
                            // Retrieve results of completed futures
                            FileUploadResponse imageResponse = imageFuture.join();
                            FileUploadResponse audioResponse = audioFuture.join();
                            MusicRequest musicRequest = MusicRequest.builder()
                                    .musicName(musicName)
                                    .musicUrl(audioResponse.getUrl())
                                    .thumbnail(imageResponse.getUrl())
                                    .uploadTime(LocalDateTime.now().toString())
                                    .build();

                            return customMessageSender.customEventSender(exchange, routingKey, true, musicRequest, MusicResponse.class);
                        } finally {
                            SecurityContextHolder.clearContext();
                        }
                    }).exceptionally(ex -> {
                        // Handle exceptions in the CompletableFuture
                        Throwable cause = ex.getCause();
                        if(cause instanceof CompletionException) {
                            throw new AuthenException(((AuthenException) cause).getErrorCode()); // Re-throw for outer handling
                        }

                        log.error("Unhandled exception from CompletableFuture: {}", ex.getMessage());
                        throw new AuthenException(ErrorCode.UNCATEGORIZED_EXCEPTION);
                    });

        } catch (CompletionException e) {
            log.error("CompletionException: {}", e.getMessage());
            throw new AuthenException( ((AuthenException)(e.getCause())).getErrorCode() ); // Re-throw for outer handling
        } catch (AuthenException e) {
            log.error("Authen: {}", e.getMessage());
            throw new AuthenException(e.getErrorCode());
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            throw new AuthenException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

}
