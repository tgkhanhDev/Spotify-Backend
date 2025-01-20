package music_service.mapper;

import music_service.dto.musicDto.response.MusicResponse;
import music_service.model.Music;
import music_service.model.MusicEls;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = ArtistCollaborationMapper.class)
public interface MusicElsMapper {

    MusicResponse toMusicResponse(MusicEls musicEls);
    List<MusicResponse> toMusicResponseList(List<MusicEls> musicElsList);
}
