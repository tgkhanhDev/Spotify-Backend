package music_service.producer;

import music_service.config.CustomMessageSender;
import music_service.dto.authenticationDto.request.IntrospectRequest;
import music_service.dto.authenticationDto.response.IntrospectResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthProducer {

    @Value("${rabbitmq.exchange.name}")
    String exchange;
    CustomMessageSender customMessageSender;

    public AuthProducer(CustomMessageSender customMessageSender) {
        this.customMessageSender = customMessageSender;
    }

    public IntrospectResponse introspectToken(IntrospectRequest introspectRequest) {
        String routingKey = "auth.introspect";
        return customMessageSender.customEventSender(exchange, routingKey, false, introspectRequest, IntrospectResponse.class);
    }

}
