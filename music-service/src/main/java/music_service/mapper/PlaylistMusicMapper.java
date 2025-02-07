package music_service.mapper;

import music_service.dto.playlistDto.response.PlaylistOverallResponse;
import music_service.dto.playlistDto.response.PlaylistResponse;
import music_service.dto.playlistMusicDto.response.PlaylistMusicOnlyMusicResponse;
import music_service.model.Playlist;
import music_service.model.PlaylistMusic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {PlaylistMapper.class, ArtistCollaborationMapper.class})
public interface PlaylistMusicMapper {

    @Mappings({
            @Mapping(source = "music.id", target = "id"),
            @Mapping(source = "music.musicName", target = "musicName"),
            @Mapping(source = "music.thumbnail", target = "thumbnail"),
            @Mapping(source = "music.uploadTime", target = "uploadTime"),
            @Mapping(source = "music.musicUrl", target = "musicUrl"),
            @Mapping(source = "music.artistCollaboration", target = "artistCollaboration")
    })
    PlaylistMusicOnlyMusicResponse toPlaylistMusicOnlyMusicResponse(PlaylistMusic playlistMusic);

}
