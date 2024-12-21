package music_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
//@EnableMethodSecurity
public class SecurityConfig {

    private final String[] AUTH_WHITELIST = {
            // -- Swagger UI v2
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/auth/**",
            "/authen-service/**"
    };

    private final String[] PUBLIC_ENDPOINTS_POST = {
            "/user/create-account"
    };

    private final String[] PUBLIC_ENDPOINTS_GET = {
            "/playlist",
            "/music/get-all"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request -> request
                .requestMatchers(AUTH_WHITELIST).permitAll()
                .requestMatchers(PUBLIC_ENDPOINTS_POST).permitAll()
                .requestMatchers(PUBLIC_ENDPOINTS_GET).permitAll()
                //                        .requestMatchers(HttpMethod.GET, "/account").hasAuthority("ROLE_ADMIN")
                .anyRequest()
                .authenticated());

        // OAuth2 //decoder: decoder is a interface, and use for decode a token
//        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
//                        .decoder(customJwtDecoder)
//                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
//                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));

        httpSecurity.csrf(AbstractHttpConfigurer::disable);


        return httpSecurity.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
