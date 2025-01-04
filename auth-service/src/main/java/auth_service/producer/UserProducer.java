package auth_service.producer;

import auth_service.config.CustomMessageSender;
import auth_service.dto.accountDto.request.UpdateAccountRequest;
import auth_service.dto.accountDto.request.UpdateUserInfoRequest;
import auth_service.dto.authenticationDto.request.AuthenticationRequest;
import auth_service.dto.authenticationDto.request.IntrospectRequest;
import auth_service.dto.authenticationDto.response.AccountResponse;
import auth_service.exception.AuthenException;
import auth_service.exception.ErrorCode;
import auth_service.service.AccountService;
import auth_service.service.AuthenticationService;
import auth_service.service.EmailService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProducer {
    final AccountService accountService;
    CustomMessageSender customMessageSender;

    @Autowired
    public UserProducer(AccountService accountService, CustomMessageSender customMessageSender) {
        this.accountService = accountService;
        this.customMessageSender = customMessageSender;
    }

    @RabbitListener(queues = "${rabbitmq.user.queue.name}")
    public void userQueueListener(Object payload, Message message) throws Exception {

        String correlationId = message.getMessageProperties().getCorrelationId();
        String replyToQueue = message.getMessageProperties().getReplyTo();

        if (replyToQueue == null) {
            throw new AuthenException(ErrorCode.INVALID_MESSAGE_QUEUE_REQUEST);
        }

        String key = message.getMessageProperties().getReceivedRoutingKey();
        Jwt jwtToken = null;

        try {
            switch (key) {
                case "user.update-info":
                    jwtToken = customMessageSender.extractTokenFromMessage(message);
                    UpdateUserInfoRequest updateUserInfoRequest = customMessageSender.decodeAndDeserializeBytes(message.getBody(), UpdateUserInfoRequest.class);
                    AccountResponse updateUserInfoResponse = accountService.updateUserInfo(updateUserInfoRequest, jwtToken);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, updateUserInfoResponse);
                    break;
                case "user.update-account-detail":
                    jwtToken = customMessageSender.extractTokenFromMessage(message);
                    UpdateAccountRequest updateAccountRequest = customMessageSender.decodeAndDeserializeBytes(message.getBody(), UpdateAccountRequest.class);
                    AccountResponse updateAccountResponse = accountService.updateAccountInfo(updateAccountRequest, jwtToken);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, updateAccountResponse);
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
