package api_gateway.controller;

import api_gateway.asynchronousTask.CreateMusicTask;
import api_gateway.config.CustomMessageSender;
import api_gateway.dto.musicDto.response.MusicResponse;
import api_gateway.exception.AuthenException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/music")
@Tag(name = "Music")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MusicController {

    @Value("${rabbitmq.exchange.name}")
    String exchange;
    CustomMessageSender customMessageSender;
    final CreateMusicTask createMusicTask;

    @Autowired
    public MusicController(CustomMessageSender customMessageSender, CreateMusicTask createMusicTask) {
        this.customMessageSender = customMessageSender;
        this.createMusicTask = createMusicTask;
    }


    @GetMapping("/get-all")
    @Operation(summary = "?Get music pagination with filter")
    @Cacheable(cacheNames = "music", key = "#root.methodName")
    public List<MusicResponse> getAllMusic(@RequestParam(required = false) String name) {
//        System.out.println("No Cache");
        String routingKey = "music.get-pagination-with-filter";
        return customMessageSender.customEventSender(exchange, routingKey, false, name, List.class);

//        return musicService.getAllMusic();
    }

    @GetMapping("/get-by-artist/{userId}")
    @Operation(summary = "Get all music by artist")
    public List<MusicResponse> getAllMusicByArtist(@RequestParam(required = true) String userId) {
        String routingKey = "music.get-music-by-artist";
        return customMessageSender.customEventSender(exchange, routingKey, false, userId, List.class);
    }

    @PostMapping(value = "/add-music", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Add Music for Subscriber")
    public MusicResponse addMusic(
            @RequestParam("thumbnail") MultipartFile thumbnail,
            @RequestParam("musicUrl") MultipartFile musicUrl,
            @RequestParam("musicName") String musicName
    ) {
        String routingKey = "music.add-music-for-artist";
        return createMusicTask.addMusic(routingKey, thumbnail, musicUrl, musicName).join();
    }
    @DeleteMapping("/delete-music/{musicId}")
    @Operation(summary = "*Delete music by id")
    public MusicResponse deleteMusic(@PathVariable String musicId) {
        String routingKey = "music.delete-music-by-id";
        return customMessageSender.customEventSender(exchange, routingKey, true, musicId, MusicResponse.class);
    }

}

