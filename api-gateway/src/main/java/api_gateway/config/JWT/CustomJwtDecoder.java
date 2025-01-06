package api_gateway.config.JWT;


import api_gateway.dto.authenticationDto.response.IntrospectResponse;
import api_gateway.exception.AuthenException;
import api_gateway.exception.ErrorCode;
import api_gateway.producer.AuthProducer;
import lombok.extern.slf4j.Slf4j;
import api_gateway.dto.authenticationDto.request.IntrospectRequest;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;


import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

@Slf4j
@Component
public class CustomJwtDecoder implements JwtDecoder {

    @Value("${SIGNER_KEY}")
    private String signerKey;

    private NimbusJwtDecoder nimbusJwtDecoder = null;
    private AuthProducer authProducer;

    @Autowired
    public CustomJwtDecoder(AuthProducer authProducer) {
        this.authProducer = authProducer;
    }

    @Override
    public Jwt decode(String token) throws JwtException {

        try {
            IntrospectResponse response = authProducer.introspectToken(IntrospectRequest.builder().token(token).build());
            if (!response.isValid()) throw new JwtException("Token invalid");
        } catch (JwtException e) {
            throw new JwtException(e.getMessage());
        }

        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return nimbusJwtDecoder.decode(token);
    }


    //*Also will decode here
    public Jwt extractTokenFromMessage(Message message) {
        String jwtToken = (String) message.getMessageProperties().getHeaders().get("Authorization");
        if (jwtToken == null) {
            throw new AuthenException(ErrorCode.INVALID_TOKEN);
        }

        Jwt decodedJwt = this.decode(jwtToken);

        return decodedJwt;
    }

}
