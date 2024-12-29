package media_service.service;

import media_service.dto.playlistDto.request.UpdatePlaylistMusicRequest;
import media_service.dto.playlistDto.request.UpdatePlaylistRequest;
import media_service.dto.playlistDto.response.PlaylistOverallResponse;
import media_service.dto.playlistDto.response.PlaylistResponse;

import java.util.List;
import java.util.UUID;

public interface PlaylistService {
    List<PlaylistOverallResponse> getAllUserPlaylist();
    PlaylistResponse getPlaylistById(UUID playlistId);
    PlaylistResponse createPlaylist();

    PlaylistResponse deletePlaylistById(UUID playlistId);

    PlaylistResponse updatePlaylistInfo(UpdatePlaylistRequest playlist);

    PlaylistResponse addPlaylistMusic(UpdatePlaylistMusicRequest request);

    PlaylistResponse removePlaylistMusic(UpdatePlaylistMusicRequest request);

}
