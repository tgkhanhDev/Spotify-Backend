package music_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import music_service.dto.playlistDto.request.UpdatePlaylistRequest;
import music_service.dto.playlistDto.response.PlaylistOverallResponse;
import music_service.dto.playlistDto.response.PlaylistResponse;
import music_service.model.Playlist;
import io.swagger.v3.oas.annotations.tags.Tag;
import music_service.service.PlaylistService;
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
    public PlaylistResponse addMusicToPlaylist(UUID playlistId, UUID musicId) {

        return null;
    }


}
