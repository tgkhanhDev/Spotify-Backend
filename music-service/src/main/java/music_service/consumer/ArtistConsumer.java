package music_service.consumer;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import music_service.config.CustomMessageSender;
import music_service.config.JWT.CustomJwtDecoder;
import music_service.dto.artistCollaborationDto.response.ArtistGeneralResponse;
import music_service.dto.authenticationDto.response.AccountResponse;
import music_service.exception.AuthenException;
import music_service.exception.ErrorCode;
import music_service.service.ArtistService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ArtistConsumer {
    //    final AccountRe
    RabbitTemplate rabbitTemplate;
    CustomMessageSender customMessageSender;
    CustomJwtDecoder customJwtDecoder;
    final ArtistService artistService;

    public ArtistConsumer(RabbitTemplate rabbitTemplate, CustomMessageSender customMessageSender, CustomJwtDecoder customJwtDecoder, ArtistService artistService) {
        this.rabbitTemplate = rabbitTemplate;
        this.customMessageSender = customMessageSender;
        this.customJwtDecoder = customJwtDecoder;
        this.artistService = artistService;
    }

    @RabbitListener(queues = "${rabbitmq.artist.queue.name}")
    public void ArtistListener(Object payload, Message message) throws Exception {

        String correlationId = message.getMessageProperties().getCorrelationId();
        String replyToQueue = message.getMessageProperties().getReplyTo();

        if (replyToQueue == null) {
            throw new AuthenException(ErrorCode.INVALID_MESSAGE_QUEUE_REQUEST);
        }

        String key = message.getMessageProperties().getReceivedRoutingKey();
        Jwt jwtToken = null;

        try {
            switch (key) {
                case "artist.get-artist-filter-by-name":
//                    jwtToken = customJwtDecoder.extractTokenFromMessage(message);
                    String nameFilter = customMessageSender.decodeAndDeserializeBytes(message.getBody(), String.class);
                    List<ArtistGeneralResponse> artistList = artistService.findArtistByName(nameFilter);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, artistList);
                    break;
                case "artist.become-artist":
                    jwtToken = customJwtDecoder.extractTokenFromMessage(message);
                    AccountResponse accountUpdated = artistService.becomeArtist(jwtToken);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, accountUpdated);
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
