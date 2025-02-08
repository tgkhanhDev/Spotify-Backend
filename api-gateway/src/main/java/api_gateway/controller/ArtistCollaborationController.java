package api_gateway.controller;

import api_gateway.config.CustomMessageSender;
import api_gateway.dto.artistCollaborationDto.response.ArtistGeneralResponse;
import api_gateway.dto.authenticationDto.response.AccountResponse;
import api_gateway.dto.musicDto.response.MusicResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/artist")
@Tag(name = "Artists")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ArtistCollaborationController {

    @Value("${rabbitmq.exchange.name}")
    String exchange;
    CustomMessageSender customMessageSender;
    @Autowired
    public ArtistCollaborationController(CustomMessageSender customMessageSender) {
        this.customMessageSender = customMessageSender;
    }


    @GetMapping("/get-all-filter")
    @Operation(summary = "Get artists with filter limit 5")
    public List<ArtistGeneralResponse> getArtistFilterByName(@RequestParam(required = false) String name) {
        String routingKey = "artist.get-artist-filter-by-name";
        return customMessageSender.customEventSender(exchange, routingKey, false, name, List.class);
    }

    @GetMapping("/get-all")
    @Operation(summary = "Get artists")
    public List<ArtistGeneralResponse> getAllArtist() {
        String routingKey = "artist.get-all-artist";
        return customMessageSender.customEventSender(exchange, routingKey, false, null, List.class);
    }

    @PatchMapping("/become-artist")
    @Operation(summary = "*Become an Artist")
    public AccountResponse becomeArtist() {
        String routingKey = "artist.become-artist";
        return customMessageSender.customEventSender(exchange, routingKey, true, null, AccountResponse.class);
    }
}
