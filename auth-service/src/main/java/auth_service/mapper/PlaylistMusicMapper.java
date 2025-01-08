package auth_service.mapper;

import auth_service.model.PlaylistMusic;
import auth_service.dto.playlistMusicDto.response.PlaylistMusicOnlyMusicResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {PlaylistMapper.class, ArtistCollaborationMapper.class})
public interface PlaylistMusicMapper {

    @Mappings({
            @Mapping(source = "music.id", target = "id"),
            @Mapping(source = "music.musicName", target = "musicName"),
            @Mapping(source = "music.thumbnail", target = "thumbnail"),
            @Mapping(source = "music.uploadTime", target = "uploadTime"),
            @Mapping(source = "music.artistCollaboration", target = "artistCollaboration")
    })
    PlaylistMusicOnlyMusicResponse toPlaylistMusicOnlyMusicResponse(PlaylistMusic playlistMusic);

}
