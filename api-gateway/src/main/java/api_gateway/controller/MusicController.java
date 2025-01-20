package api_gateway.controller;

import api_gateway.config.CustomMessageSender;
import api_gateway.dto.musicDto.response.MusicResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/music")
@Tag(name = "Music")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MusicController {

    @Value("${rabbitmq.exchange.name}")
    String exchange;
    CustomMessageSender customMessageSender;
    @Autowired
    public MusicController(CustomMessageSender customMessageSender) {
        this.customMessageSender = customMessageSender;
    }


    @GetMapping("/get-all")
    @Operation(summary = "?*Get music pagination with filter")
    @Cacheable(cacheNames = "music", key = "#root.methodName")
    public List<MusicResponse> getAllMusic(@RequestParam(required = false) String name ) {
//        System.out.println("No Cache");
        String routingKey = "music.get-pagination-with-filter";
        return customMessageSender.customEventSender(exchange, routingKey, false, name, List.class);

//        return musicService.getAllMusic();
    }
}
