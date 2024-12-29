package media_service.mapper;

import media_service.dto.musicDto.response.MusicResponse;
import media_service.model.Music;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = ArtistCollaborationMapper.class)
public interface MusicMapper {

    MusicResponse toMusicResponse(Music music);
    List<MusicResponse> toMusicResponseList(List<Music> music);
}
