package music_service.consumer;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import music_service.config.CustomMessageSender;
import music_service.config.JWT.CustomJwtDecoder;
import music_service.dto.authenticationDto.response.AccountResponse;
import music_service.dto.playlistDto.request.UpdatePlaylistMusicRequest;
import music_service.dto.playlistDto.request.UpdatePlaylistRequest;
import music_service.dto.playlistDto.response.PlaylistDetailByUser;
import music_service.dto.playlistDto.response.PlaylistOverallResponse;
import music_service.dto.playlistDto.response.PlaylistResponse;
import music_service.exception.AuthenException;
import music_service.exception.ErrorCode;
import music_service.producer.UserProducer;
import music_service.service.PlaylistService;
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
public class PlaylistConsumer {

    //Producer
    final UserProducer userProducer;

    final PlaylistService playlistService;
    RabbitTemplate rabbitTemplate;
    CustomMessageSender customMessageSender;
    CustomJwtDecoder customJwtDecoder;


    @Autowired
    public PlaylistConsumer(UserProducer userProducer, PlaylistService playlistService, RabbitTemplate rabbitTemplate, CustomMessageSender customMessageSender, CustomJwtDecoder customJwtDecoder) {
        this.userProducer = userProducer;
        this.playlistService = playlistService;
        this.rabbitTemplate = rabbitTemplate;
        this.customMessageSender = customMessageSender;
        this.customJwtDecoder = customJwtDecoder;
    }


    @RabbitListener(queues = "${rabbitmq.playlist.queue.name}")
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
                case "playlist.get-all-playlist":
                    List<PlaylistOverallResponse> playlists = playlistService.getAllPlaylist();
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, playlists);
                    break;
                case "playlist.get-all-playlist-by-user":
                    jwtToken = customJwtDecoder.extractTokenFromMessage(message);
                    //Get accountResponse From UserQueue
                    AccountResponse accountResponse = userProducer.getUserInfo(message.getMessageProperties().getHeader("Authorization").toString());

                    //Get PlaylistResponse From PlaylistQueue
                    List<PlaylistOverallResponse> playlistOverallResponseList = playlistService.getAllUserPlaylist(jwtToken);


                    //Send Response
                    PlaylistDetailByUser response = PlaylistDetailByUser.builder()
                            .account(accountResponse)
                            .playlists(playlistOverallResponseList)
                            .build();

                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, response);
                    break;
                case "playlist.get-playlist-by-id":
                    UUID playlistId = customMessageSender.decodeAndDeserializeBytes(message.getBody(), UUID.class);
                    PlaylistResponse playlistResponse = playlistService.getPlaylistById(playlistId);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, playlistResponse);
                    break;
                case "playlist.create-playlist":
                    jwtToken = customJwtDecoder.extractTokenFromMessage(message);
                    PlaylistResponse createPlaylistResponse = playlistService.createPlaylist(jwtToken);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, createPlaylistResponse);
                    break;
                case "playlist.delete-playlist":
                    jwtToken = customJwtDecoder.extractTokenFromMessage(message);
                    UUID delPlaylistId = customMessageSender.decodeAndDeserializeBytes(message.getBody(), UUID.class);
                    PlaylistResponse delPlaylistResponse = playlistService.deletePlaylistById(delPlaylistId, jwtToken);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, delPlaylistResponse);
                    break;
                case "playlist.update-playlist":
                    jwtToken = customJwtDecoder.extractTokenFromMessage(message);
                    UpdatePlaylistRequest updatePlaylistRequest = customMessageSender.decodeAndDeserializeBytes(message.getBody(), UpdatePlaylistRequest.class);
                    PlaylistResponse updatePlaylistResponse = playlistService.updatePlaylistInfo(updatePlaylistRequest, jwtToken);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, updatePlaylistResponse);
                    break;
                case "playlist.add-music-to-playlist":
                    jwtToken = customJwtDecoder.extractTokenFromMessage(message);
                    UpdatePlaylistMusicRequest updatePlaylistMusicRequest = customMessageSender.decodeAndDeserializeBytes(message.getBody(), UpdatePlaylistMusicRequest.class);
                    PlaylistResponse updatePlaylistMusicResponse = playlistService.addPlaylistMusic(updatePlaylistMusicRequest, jwtToken);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, updatePlaylistMusicResponse);
                    break;
                case "playlist.remove-music-from-playlist":
                    jwtToken = customJwtDecoder.extractTokenFromMessage(message);
                    UpdatePlaylistMusicRequest removePlaylistMusicRequest = customMessageSender.decodeAndDeserializeBytes(message.getBody(), UpdatePlaylistMusicRequest.class);
                    PlaylistResponse removePlaylistMusicResponse = playlistService.removePlaylistMusic(removePlaylistMusicRequest, jwtToken);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, removePlaylistMusicResponse);
                    break;
                case "playlist.get-playlist-by-artist":
                    UUID artistId = customMessageSender.decodeAndDeserializeBytes(message.getBody(), UUID.class);
                    List<PlaylistOverallResponse> getPlaylistByArtistResponse = playlistService.getPlaylistByArtist(artistId);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, getPlaylistByArtistResponse);
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
