package music_service.controller;

import music_service.dto.artistCollaborationDto.response.ArtistCollaborationResponse;
import music_service.mapper.ArtistCollaborationMapper;
import music_service.model.ArtistCollaboration;
import music_service.repository.ArtistCollaborationRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/playlist")
@Tag(name = "Shopping Cart")
public class PlaylistController {

    private final ArtistCollaborationRepository artistRepository;
    private final ArtistCollaborationMapper artistCollaborationMapper;

    public PlaylistController(ArtistCollaborationRepository artistRepository, ArtistCollaborationMapper artistCollaborationMapper) {
        this.artistRepository = artistRepository;
        this.artistCollaborationMapper = artistCollaborationMapper;
    }

    @GetMapping("")
    public List<ArtistCollaborationResponse> getAllPlaylist() {
//        return Object.builder().build
        List<ArtistCollaboration> artistCollaboration = artistRepository.findAll();
        return artistCollaborationMapper.toArtistCollaborationResponseList(artistCollaboration);
    }


}
