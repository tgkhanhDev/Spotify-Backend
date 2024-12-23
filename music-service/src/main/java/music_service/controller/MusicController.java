package music_service.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import music_service.dto.musicDto.response.MusicResponse;
import music_service.service.MusicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/music")
@Tag(name = "Music")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MusicController {

    MusicService musicService;

    @Autowired
    public MusicController(MusicService musicService) {
        this.musicService = musicService;
    }

    @GetMapping("/get-all")
    @Operation(summary = "Test Only", description = "Test Only")
//    @SecurityRequirement(name = "
//    BearerAuth") // Apply security globally to the class
    public List<MusicResponse> getAllMusic() {
        return musicService.getAllMusic();
    }
}
