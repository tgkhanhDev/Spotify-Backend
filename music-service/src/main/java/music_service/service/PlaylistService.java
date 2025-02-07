package music_service.service;

import music_service.dto.playlistDto.request.UpdatePlaylistMusicRequest;
import music_service.dto.playlistDto.request.UpdatePlaylistRequest;
import music_service.dto.playlistDto.response.PlaylistDetailByUser;
import music_service.dto.playlistDto.response.PlaylistOverallResponse;
import music_service.dto.playlistDto.response.PlaylistResponse;
import music_service.model.Playlist;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.UUID;

public interface PlaylistService {

    List<PlaylistOverallResponse> getAllPlaylist();
    List<PlaylistOverallResponse> getAllUserPlaylist(Jwt jwtToken);
    PlaylistResponse getPlaylistById(UUID playlistId);
    PlaylistResponse createPlaylist(Jwt jwtToken);

    PlaylistResponse deletePlaylistById(UUID playlistId, Jwt jwtToken);

    PlaylistResponse updatePlaylistInfo(UpdatePlaylistRequest playlist, Jwt jwtToken);

    PlaylistResponse addPlaylistMusic(UpdatePlaylistMusicRequest request, Jwt jwtToken);

    PlaylistResponse removePlaylistMusic(UpdatePlaylistMusicRequest request, Jwt jwtToken);

    List<PlaylistOverallResponse> getPlaylistByArtist(UUID artistId);
}
