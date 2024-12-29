package media_service.mapper;

import media_service.dto.playlistDto.response.PlaylistOverallResponse;
import media_service.dto.playlistDto.response.PlaylistResponse;
import media_service.model.Playlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {PlaylistMusicMapper.class})
public interface PlaylistMapper {

    @Mapping(source = "id", target = "playlistId")
    PlaylistOverallResponse toPlaylistOverallResponse(Playlist playlist);
    List<PlaylistOverallResponse> toPlaylistOverallResponseList(List<Playlist> playlist);

    //playlistMusicSet
    @Mappings({
            @Mapping(source = "id", target = "playlistId"),
            @Mapping(source = "playlistMusicSet", target = "musics")
    })
    PlaylistResponse toPlaylistResponse(Playlist playlist);

}
