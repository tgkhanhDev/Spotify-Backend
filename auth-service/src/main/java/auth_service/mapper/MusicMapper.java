package auth_service.mapper;

import auth_service.model.Music;
import auth_service.dto.musicDto.response.MusicResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = ArtistCollaborationMapper.class)
public interface MusicMapper {

    MusicResponse toMusicResponse(Music music);
    List<MusicResponse> toMusicResponseList(List<Music> music);
}
