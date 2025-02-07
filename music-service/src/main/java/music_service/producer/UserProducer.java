package music_service.producer;

import music_service.config.CustomMessageSender;
import music_service.dto.authenticationDto.request.IntrospectRequest;
import music_service.dto.authenticationDto.response.AccountResponse;
import music_service.dto.authenticationDto.response.IntrospectResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserProducer {

    @Value("${rabbitmq.exchange.name}")
    String exchange;
    CustomMessageSender customMessageSender;

    public UserProducer(CustomMessageSender customMessageSender) {
        this.customMessageSender = customMessageSender;
    }

    public AccountResponse getUserInfo(String jwtHeader) {
        String routingKey = "user.get-user-info";
        AccountResponse acc = customMessageSender.customEventSenderWithJWT(exchange, routingKey, jwtHeader, null, AccountResponse.class);
        return acc;
    }

}
