package music_service.consumer;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import music_service.config.CustomMessageSender;
import music_service.config.JWT.CustomJwtDecoder;
import music_service.dto.musicDto.request.MusicRequest;
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
                    try{
                        jwtToken = customJwtDecoder.extractTokenFromMessage(message);
                    }catch (AuthenException e){
                        jwtToken = null;
                    }
                    String searchFilter = customMessageSender.decodeAndDeserializeBytes(message.getBody(), String.class);
                    List<MusicResponse> musicResponseList = musicService.getAllMusic(jwtToken, searchFilter);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, musicResponseList);
                    break;
                case "music.add-music-for-artist":
                    jwtToken = customJwtDecoder.extractTokenFromMessage(message);
                    MusicRequest musicRequest = customMessageSender.decodeAndDeserializeBytes(message.getBody(), MusicRequest.class);
                    MusicResponse musicResponse = musicService.addMusic(jwtToken, musicRequest);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, musicResponse);
                    break;
                case "music.get-music-by-artist":
                    String artistId = customMessageSender.decodeAndDeserializeBytes(message.getBody(), String.class);
                    List<MusicResponse> listMusicOfArtist = musicService.getAllMusicByArtist(artistId);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, listMusicOfArtist);
                    break;
                case "music.delete-music-by-id":
                    jwtToken = customJwtDecoder.extractTokenFromMessage(message);
                    String musicId = customMessageSender.decodeAndDeserializeBytes(message.getBody(), String.class);
                    MusicResponse deletedMusic = musicService.deleteMusic(jwtToken, musicId);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, deletedMusic);
                    break;
                case "music.generate-music-queue-by-music-id":
//                    jwtToken = customJwtDecoder.extractTokenFromMessage(message); //not use yet
                    String musicQueueId = customMessageSender.decodeAndDeserializeBytes(message.getBody(), String.class);
                    List<MusicResponse> musicQueue = musicService.generateMusicQueueByMusicId(musicQueueId);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, musicQueue);

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
