package music_service.consumer;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import music_service.config.CustomMessageSender;
import music_service.config.JWT.CustomJwtDecoder;
import music_service.dto.musicDto.response.MusicResponse;
import music_service.dto.playlistDto.request.UpdatePlaylistMusicRequest;
import music_service.dto.playlistDto.request.UpdatePlaylistRequest;
import music_service.dto.playlistDto.response.PlaylistOverallResponse;
import music_service.dto.playlistDto.response.PlaylistResponse;
import music_service.exception.AuthenException;
import music_service.exception.ErrorCode;
import music_service.service.MusicService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MusicConsumer {
    final MusicService musicService;
    RabbitTemplate rabbitTemplate;
    CustomMessageSender customMessageSender;
    CustomJwtDecoder customJwtDecoder;

    public MusicConsumer(MusicService musicService, RabbitTemplate rabbitTemplate, CustomMessageSender customMessageSender, CustomJwtDecoder customJwtDecoder) {
        this.musicService = musicService;
        this.rabbitTemplate = rabbitTemplate;
        this.customMessageSender = customMessageSender;
        this.customJwtDecoder = customJwtDecoder;
    }


    @RabbitListener(queues = "${rabbitmq.music.queue.name}")
    public void playlistQueueListener(Object payload, Message message) throws Exception {

        String correlationId = message.getMessageProperties().getCorrelationId();
        String replyToQueue = message.getMessageProperties().getReplyTo();

        if (replyToQueue == null) {
            throw new AuthenException(ErrorCode.INVALID_MESSAGE_QUEUE_REQUEST);
        }

        String key = message.getMessageProperties().getReceivedRoutingKey();
        Jwt jwtToken = null;

        try {
            switch (key) {
                case "music.get-pagination-with-filter":
//                    jwtToken = customJwtDecoder.extractTokenFromMessage(message);
                    List<MusicResponse> musicResponseList = musicService.getAllMusic();
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, musicResponseList);
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
