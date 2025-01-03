package api_gateway.config.JWT;


import api_gateway.exception.AuthenException;
import api_gateway.exception.ErrorCode;
import api_gateway.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import api_gateway.dto.authenticationDto.request.IntrospectRequest;
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
import java.util.Objects;

@Slf4j
@Component
public class CustomJwtDecoder implements JwtDecoder {

    @Value("${SIGNER_KEY}")
    private String signerKey;

    @Autowired
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {

        try {
            var response = authenticationService.introspect(
                    IntrospectRequest.builder().token(token).build());

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

    // Done, Ctrl Z to know the bug
    public String extractJwtToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the authentication is valid
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenException(ErrorCode.UNAUTHORIZED);
        }

        // Extract the Jwt object from the credentials (or principal)
        Object credentials = authentication.getCredentials();

        // Ensure the credentials are of type Jwt
        if (credentials instanceof Jwt) {
            Jwt jwt = (Jwt) credentials;
            return jwt.getTokenValue();  // Get the raw JWT token as a string
        }

        // If the credentials aren't of type Jwt, throw an exception
        throw new AuthenException(ErrorCode.INVALID_TOKEN);
    }


}
