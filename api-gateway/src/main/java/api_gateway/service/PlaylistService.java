package api_gateway.service;

import api_gateway.dto.playlistDto.request.UpdatePlaylistMusicRequest;
import api_gateway.dto.playlistDto.request.UpdatePlaylistRequest;
import api_gateway.dto.playlistDto.response.PlaylistOverallResponse;
import api_gateway.dto.playlistDto.response.PlaylistResponse;

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
