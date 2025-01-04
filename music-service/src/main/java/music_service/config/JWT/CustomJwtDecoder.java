package music_service.config.JWT;


import com.nimbusds.jose.JOSEException;
import lombok.extern.slf4j.Slf4j;
import music_service.config.CustomMessageSender;
import music_service.dto.authenticationDto.request.IntrospectRequest;
import music_service.dto.authenticationDto.response.IntrospectResponse;
import music_service.exception.AuthenException;
import music_service.exception.ErrorCode;
import music_service.producer.AuthProducer;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;


import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
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
            System.out.println("Introspect Response: " + response);
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

        System.out.println("DecodedJwt: " + decodedJwt);

        return decodedJwt;
    }

}
