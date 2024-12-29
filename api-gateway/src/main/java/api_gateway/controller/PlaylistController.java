package api_gateway.controller;

import api_gateway.service.PlaylistService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import api_gateway.dto.playlistDto.request.UpdatePlaylistMusicRequest;
import api_gateway.dto.playlistDto.request.UpdatePlaylistRequest;
import api_gateway.dto.playlistDto.response.PlaylistOverallResponse;
import api_gateway.dto.playlistDto.response.PlaylistResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/playlist")
@Tag(name = "Playlist")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlaylistController {

    PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }


    @GetMapping("")
    @Operation(summary = "*Get all playlist by user")
    @Cacheable(cacheNames = "playlist", key = "#root.methodName")
    public List<PlaylistOverallResponse> getAllPlaylistByUser() {
         return playlistService.getAllUserPlaylist();
//        List<ArtistCollaboration> artistCollaboration = artistRepository.findAll();
//        return artistCollaborationMapper.toArtistCollaborationResponseList(artistCollaboration);
    }

    @GetMapping("/{playlistId}")
    @Operation(summary = "Get playlist details")
    public PlaylistResponse getPlaylistById(@RequestParam UUID playlistId) {
        return playlistService.getPlaylistById(playlistId);
    }

    @PostMapping("/create")
    @Operation(summary = "*Create new playlist")
    public PlaylistResponse createPlaylist() {
        return playlistService.createPlaylist();
    }

    @DeleteMapping("/delete/{playlistId}")
    @Operation(summary = "*Delete playlist")
    public PlaylistResponse deletePlaylist(@RequestParam UUID playlistId) {
        return playlistService.deletePlaylistById(playlistId);
    }

    @PatchMapping("/update")
    @Operation(summary = "*Update playlist")
    public PlaylistResponse updatePlaylist(@RequestBody() @Valid UpdatePlaylistRequest playlist) {
        return playlistService.updatePlaylistInfo(playlist);
    }

    @PostMapping("/add-song")
    @Operation(summary = "*Add music to playlist")
    public PlaylistResponse addMusicToPlaylist(@RequestBody() UpdatePlaylistMusicRequest request) {
        return playlistService.addPlaylistMusic(request);
    }

    @DeleteMapping("/remove-song")
    @Operation(summary = "*Remove music from playlist")
    public PlaylistResponse removeMusicFromPlaylist(@RequestBody() UpdatePlaylistMusicRequest request) {
        return playlistService.removePlaylistMusic(request);
    }


}
