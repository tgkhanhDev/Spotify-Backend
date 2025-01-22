package api_gateway.mapper;

import api_gateway.dto.musicDto.request.MusicRequest;
import api_gateway.model.Music;
import api_gateway.dto.musicDto.response.MusicResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = ArtistCollaborationMapper.class)
public interface MusicMapper {

    Music toMusic(MusicRequest musicRequest);

    MusicResponse toMusicResponse(Music music);
    List<MusicResponse> toMusicResponseList(List<Music> music);

}