package api_gateway.producer;

import api_gateway.config.CustomMessageSender;
import api_gateway.dto.authenticationDto.request.IntrospectRequest;
import api_gateway.dto.authenticationDto.response.IntrospectResponse;
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
