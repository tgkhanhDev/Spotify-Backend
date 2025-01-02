package auth_service.service;

import auth_service.dto.playlistDto.request.UpdatePlaylistMusicRequest;
import auth_service.dto.playlistDto.request.UpdatePlaylistRequest;
import auth_service.dto.playlistDto.response.PlaylistOverallResponse;
import auth_service.dto.playlistDto.response.PlaylistResponse;

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
