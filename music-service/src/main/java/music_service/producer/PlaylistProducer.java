package music_service.producer;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import music_service.config.CustomMessageSender;
import music_service.dto.playlistDto.request.UpdatePlaylistMusicRequest;
import music_service.dto.playlistDto.request.UpdatePlaylistRequest;
import music_service.dto.playlistDto.response.PlaylistResponse;
import music_service.exception.AuthenException;
import music_service.exception.ErrorCode;
import music_service.service.PlaylistService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlaylistProducer {
    final PlaylistService playlistService;
    RabbitTemplate rabbitTemplate;
    CustomMessageSender customMessageSender;

    public PlaylistProducer(PlaylistService playlistService, RabbitTemplate rabbitTemplate, CustomMessageSender customMessageSender) {
        this.playlistService = playlistService;
        this.rabbitTemplate = rabbitTemplate;
        this.customMessageSender = customMessageSender;
    }


    @RabbitListener(queues = "${rabbitmq.product.queue.name}")
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
                case "playlist.get-all-playlist-by-user":
                    jwtToken = customMessageSender.extractTokenFromMessage(message);
                    break;
                case "playlist.get-playlist-by-id":
                    UUID playlistId = customMessageSender.decodeAndDeserializeBytes(message.getBody(), UUID.class);
                    PlaylistResponse playlistResponse = playlistService.getPlaylistById(playlistId);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, playlistResponse);
                    break;
                case "playlist.create-playlist":
                    jwtToken = customMessageSender.extractTokenFromMessage(message);
                    PlaylistResponse createPlaylistResponse = playlistService.createPlaylist(jwtToken);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, createPlaylistResponse);
                    break;
                case "playlist.delete-playlist":
                    jwtToken = customMessageSender.extractTokenFromMessage(message);
                    UUID delPlaylistId = customMessageSender.decodeAndDeserializeBytes(message.getBody(), UUID.class);
                    PlaylistResponse delPlaylistResponse = playlistService.deletePlaylistById(delPlaylistId, jwtToken);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, delPlaylistResponse);
                    break;
                case "playlist.update-playlist":
                    jwtToken = customMessageSender.extractTokenFromMessage(message);
                    UpdatePlaylistRequest updatePlaylistRequest = customMessageSender.decodeAndDeserializeBytes(message.getBody(), UpdatePlaylistRequest.class);
                    PlaylistResponse updatePlaylistResponse = playlistService.updatePlaylistInfo(updatePlaylistRequest, jwtToken);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, updatePlaylistResponse);
                    break;
                case "playlist.add-music-to-playlist":
                    jwtToken = customMessageSender.extractTokenFromMessage(message);
                    UpdatePlaylistMusicRequest updatePlaylistMusicRequest = customMessageSender.decodeAndDeserializeBytes(message.getBody(), UpdatePlaylistMusicRequest.class);
                    PlaylistResponse updatePlaylistMusicResponse = playlistService.addPlaylistMusic(updatePlaylistMusicRequest, jwtToken);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, updatePlaylistMusicResponse);
                    break;
                case "playlist.remove-music-from-playlist":
                    jwtToken = customMessageSender.extractTokenFromMessage(message);
                    UpdatePlaylistMusicRequest removePlaylistMusicRequest = customMessageSender.decodeAndDeserializeBytes(message.getBody(), UpdatePlaylistMusicRequest.class);
                    PlaylistResponse removePlaylistMusicResponse = playlistService.removePlaylistMusic(removePlaylistMusicRequest, jwtToken);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, removePlaylistMusicResponse);
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
