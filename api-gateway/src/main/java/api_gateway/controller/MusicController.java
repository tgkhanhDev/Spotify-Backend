package api_gateway.controller;

import api_gateway.service.MusicService;
import api_gateway.dto.musicDto.response.MusicResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(cacheNames = "music", key = "#root.methodName")
    public List<MusicResponse> getAllMusic() {
        System.out.println("No Cache");
        return musicService.getAllMusic();
    }
}
