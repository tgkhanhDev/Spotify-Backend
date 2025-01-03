package auth_service.producer;

import auth_service.config.CustomMessageSender;
import auth_service.dto.accountDto.response.CheckMailResponse;
import auth_service.dto.authenticationDto.request.*;
import auth_service.dto.authenticationDto.response.AccountResponse;
import auth_service.dto.authenticationDto.response.AuthenticationResponse;
import auth_service.dto.authenticationDto.response.EmailAuthenResponse;
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
import org.springframework.stereotype.Service;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthProducer {
    final AuthenticationService authenticationService;
    final AccountService accountService;
    final EmailService emailService;
    RabbitTemplate rabbitTemplate;
    CustomMessageSender customMessageSender;

    @Autowired
    public AuthProducer(AuthenticationService authenticationService, AccountService accountService, EmailService emailService, RabbitTemplate rabbitTemplate, CustomMessageSender customMessageSender) {
        this.authenticationService = authenticationService;
        this.accountService = accountService;
        this.emailService = emailService;
        this.rabbitTemplate = rabbitTemplate;
        this.customMessageSender = customMessageSender;
    }

    @RabbitListener(queues = "${rabbitmq.auth.queue.name}")
    public void authenQueueListener(Object payload, Message message) throws Exception {

        String correlationId = message.getMessageProperties().getCorrelationId();
        String replyToQueue = message.getMessageProperties().getReplyTo();

        if (replyToQueue == null) {
            throw new AuthenException(ErrorCode.INVALID_MESSAGE_QUEUE_REQUEST);
        }

        String key = message.getMessageProperties().getReceivedRoutingKey();

        try {
            switch (key) {
                case "auth.login":
                    AuthenticationRequest authenticationRequest = customMessageSender.decodeAndDeserializeBytes(message.getBody(), AuthenticationRequest.class);
                    AuthenticationResponse authenticationResponse = authenticationService.authenticate(authenticationRequest);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, authenticationResponse);
                    break;
                case "auth.check-email":
                    CheckEmailRequest checkEmailRequest = customMessageSender.decodeAndDeserializeBytes(message.getBody(), CheckEmailRequest.class);
                    CheckMailResponse checkMailResponse = accountService.checkMail(checkEmailRequest);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, checkMailResponse);
                    break;
                case "auth.signup":
                    CreateAccountRequest createAccountRequest = customMessageSender.decodeAndDeserializeBytes(message.getBody(), CreateAccountRequest.class);
                    AccountResponse accountResponse = accountService.registerAccount(createAccountRequest);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, accountResponse);
                    break;
                case "auth.reset-password":
                    ResetPasswordRequest resetPasswordRequest = customMessageSender.decodeAndDeserializeBytes(message.getBody(), ResetPasswordRequest.class);
                    AccountResponse accountResetPasswordResponse = authenticationService.resetPassword(resetPasswordRequest);
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, accountResetPasswordResponse);
                    break;
                case "auth.forget-password":
                    CheckEmailRequest checkEmailRequestForget = customMessageSender.decodeAndDeserializeBytes(message.getBody(), CheckEmailRequest.class);
                    CheckMailResponse checkMailResponseForget = accountService.checkMail(checkEmailRequestForget);

                    if (!checkMailResponseForget.isExisted()) {
                        EmailAuthenResponse response = EmailAuthenResponse
                                .builder()
                                .code(404)
                                .message("Email Not Found")
                                .isValid(checkMailResponseForget.isExisted())
                                .build();
                        customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, response);
                    }
                    EmailAuthenResponse emailAuthenResponse = EmailAuthenResponse
                            .builder()
                            .code(200)
                            .message("Success")
                            .isValid(emailService.sendEmail(checkEmailRequestForget.getEmail()))
                            .build();
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, emailAuthenResponse);
                    break;
                case "auth.forget-password-confirm":
                    ForgetPasswordConfirmRequest checkEmailRequestForgetConfirm = customMessageSender.decodeAndDeserializeBytes(message.getBody(), ForgetPasswordConfirmRequest.class);
                    CheckMailResponse checkMailResponseForgetConfirm = accountService.checkMail(CheckEmailRequest.builder().email(checkEmailRequestForgetConfirm.getEmail()).build());

                    if (!checkMailResponseForgetConfirm.isExisted()) {
                        EmailAuthenResponse response = EmailAuthenResponse
                                .builder()
                                .code(404)
                                .message("Email Not Found")
                                .isValid(checkMailResponseForgetConfirm.isExisted())
                                .build();
                        customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, response);
                    }
                    EmailAuthenResponse emailAuthenResponseForgetConfirm = emailService.verifyCode(checkEmailRequestForgetConfirm.getEmail(), checkEmailRequestForgetConfirm.getCode());
                    customMessageSender.sendResponseDataToProducer(correlationId, replyToQueue, emailAuthenResponseForgetConfirm);

                    break;
                default:
                    throw new AuthenException(ErrorCode.INVALID_MESSAGE_QUEUE_REQUEST);
            }
        } catch (AuthenException e) {
            log.info("Catch Exception: " + e.getMessage());
            // Handle AuthenException and send error response back to producer
            customMessageSender.sendErrorCodeToProducer(correlationId, replyToQueue, e.getErrorCode());
        } catch (Exception e) {
            log.error("Error Exception: " + e.getMessage());
            // Handle any other unexpected exceptions
            customMessageSender.sendErrorCodeToProducer(correlationId, replyToQueue, ErrorCode.UNCATEGORIZED_EXCEPTION);
        }


    }


}
