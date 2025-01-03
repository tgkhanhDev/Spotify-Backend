package music_service.service;

import music_service.dto.playlistDto.request.UpdatePlaylistMusicRequest;
import music_service.dto.playlistDto.request.UpdatePlaylistRequest;
import music_service.dto.playlistDto.response.PlaylistOverallResponse;
import music_service.dto.playlistDto.response.PlaylistResponse;
import music_service.model.Playlist;

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
