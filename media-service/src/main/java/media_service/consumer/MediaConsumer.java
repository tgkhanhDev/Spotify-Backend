package media_service.consumer;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import media_service.config.CustomMessageSender;
import media_service.config.JWT.CustomJwtDecoder;
import media_service.dto.fileDto.request.FileUploadRequest;
import media_service.dto.fileDto.response.FileUploadResponse;
import media_service.exception.AuthenException;
import media_service.exception.ErrorCode;
import media_service.service.FileService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MediaConsumer {
    final FileService fileService;
    RabbitTemplate rabbitTemplate;
    CustomMessageSender customMessageSender;
    CustomJwtDecoder customJwtDecoder;

    @Autowired
    public MediaConsumer(FileService fileService, RabbitTemplate rabbitTemplate, CustomMessageSender customMessageSender, CustomJwtDecoder customJwtDecoder) {
        this.fileService = fileService;
        this.rabbitTemplate = rabbitTemplate;
        this.customMessageSender = customMessageSender;
        this.customJwtDecoder = customJwtDecoder;
    }

    @RabbitListener(queues = "${rabbitmq.mediaIO.queue.name}")
    public void mediaQueueListener(Object payload, Message message) throws Exception {
        String correlationId = message.getMessageProperties().getCorrelationId();
        String replyToQueue = message.getMessageProperties().getReplyTo();

        if (replyToQueue == null) {
            throw new AuthenException(ErrorCode.INVALID_MESSAGE_QUEUE_REQUEST);
        }

        String key = message.getMessageProperties().getReceivedRoutingKey();
        Jwt jwtToken = null;

        try {
            switch (key) {
                case "media.upload-file-audio":
                    jwtToken = customJwtDecoder.extractTokenFromMessage(message);
                    FileUploadRequest requestAudio = customMessageSender.decodeAndDeserializeBytes(message.getBody(), FileUploadRequest.class);
                    FileUploadResponse uploadAudioResponse = fileService.uploadFileAudioAWS(requestAudio, jwtToken);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, uploadAudioResponse);
                    break;
                case "media.upload-file-image":
                    jwtToken = customJwtDecoder.extractTokenFromMessage(message);
                    FileUploadRequest request = customMessageSender.decodeAndDeserializeBytes(message.getBody(), FileUploadRequest.class);
                    FileUploadResponse uploadImageResponse = fileService.uploadFileImageAWS(request, jwtToken);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, uploadImageResponse);
                    break;
                default:
                    throw new AuthenException(ErrorCode.INVALID_MESSAGE_QUEUE_REQUEST);
            }
        } catch (AuthenException e) {
            log.info("Catch Exception: " + e.getMessage());
            customMessageSender.sendErrorCodeToProducer(correlationId, replyToQueue, e.getErrorCode());
        } catch (Exception e) {
            log.error("Error Exception: " + e.getMessage());
            customMessageSender.sendErrorCodeToProducer(correlationId, replyToQueue, ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

}
