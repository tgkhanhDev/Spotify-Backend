package api_gateway.controller;

import api_gateway.config.CustomMessageSender;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import api_gateway.dto.playlistDto.request.UpdatePlaylistMusicRequest;
import api_gateway.dto.playlistDto.request.UpdatePlaylistRequest;
import api_gateway.dto.playlistDto.response.PlaylistOverallResponse;
import api_gateway.dto.playlistDto.response.PlaylistResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/playlist")
@Tag(name = "Playlist")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlaylistController {

    @Value("${rabbitmq.exchange.name}")
    String exchange;
    CustomMessageSender customMessageSender;

    public PlaylistController(CustomMessageSender customMessageSender) {
        this.customMessageSender = customMessageSender;
    }


    @GetMapping("")
    @Operation(summary = "*Get all playlist by user")
    @Cacheable(cacheNames = "playlist", key = "#root.methodName")
    public List<PlaylistOverallResponse> getAllPlaylistByUser() {
        String routingKey = "playlist.get-all-playlist-by-user";
//        return customMessageSender.customEventSender(exchange, routingKey, true, null, List.class);
//        List<ArtistCollaboration> artistCollaboration = artistRepository.findAll();
//        return artistCollaborationMapper.toArtistCollaborationResponseList(artistCollaboration);
        return null;
//        return playlistService.getAllUserPlaylist();
    }

    @GetMapping("/{playlistId}")
    @Operation(summary = "Get playlist details")
    public PlaylistResponse getPlaylistById(@RequestParam UUID playlistId) {
        String routingKey = "playlist.get-playlist-by-id";
        return customMessageSender.customEventSender(exchange, routingKey, false, playlistId, PlaylistResponse.class);
//        return playlistService.getPlaylistById(playlistId);
    }

    @PostMapping("/create")
    @Operation(summary = "*Create new playlist")
    public PlaylistResponse createPlaylist() {

        String routingKey = "playlist.create-playlist";
        return customMessageSender.customEventSender(exchange, routingKey, true, null, PlaylistResponse.class);
//        return playlistService.createPlaylist();
    }

    @DeleteMapping("/delete/{playlistId}")
    @Operation(summary = "*Delete playlist")
    public PlaylistResponse deletePlaylist(@RequestParam UUID playlistId) {
        String routingKey = "playlist.delete-playlist";
        return customMessageSender.customEventSender(exchange, routingKey, false, playlistId, PlaylistResponse.class);
//        return playlistService.deletePlaylistById(playlistId);
    }

    @PatchMapping("/update")
    @Operation(summary = "*Update playlist")
    public PlaylistResponse updatePlaylist(@RequestBody() @Valid UpdatePlaylistRequest playlist) {
        String routingKey = "playlist.update-playlist";
        return customMessageSender.customEventSender(exchange, routingKey, true, playlist, PlaylistResponse.class);
//        return playlistService.updatePlaylistInfo(playlist);
    }

    @PostMapping("/add-song")
    @Operation(summary = "*Add music to playlist")
    public PlaylistResponse addMusicToPlaylist(@RequestBody() UpdatePlaylistMusicRequest request) {
        String routingKey = "playlist.add-music-to-playlist";
        return customMessageSender.customEventSender(exchange, routingKey, true, request, PlaylistResponse.class);
//        return playlistService.addPlaylistMusic(request);
    }

    @DeleteMapping("/remove-song")
    @Operation(summary = "*Remove music from playlist")
    public PlaylistResponse removeMusicFromPlaylist(@RequestBody() UpdatePlaylistMusicRequest request) {
        String routingKey = "playlist.remove-music-from-playlist";
        return customMessageSender.customEventSender(exchange, routingKey, true, request, PlaylistResponse.class);
//        return playlistService.removePlaylistMusic(request);
    }


}
