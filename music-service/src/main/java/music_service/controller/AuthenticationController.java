package music_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import music_service.dto.authenticationDto.request.AuthenticationRequest;
import music_service.dto.authenticationDto.request.LoginRequest;
import music_service.dto.authenticationDto.response.AuthenticationResponse;
import music_service.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "For Authentication")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest){
        return authenticationService.authenticate(authenticationRequest);
    }
}
