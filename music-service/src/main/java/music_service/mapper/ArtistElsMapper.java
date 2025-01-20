package music_service.mapper;

import music_service.dto.artistCollaborationDto.response.ArtistCollaborationResponse;
import music_service.dto.artistCollaborationDto.response.ArtistGeneralResponse;
import music_service.model.ArtistCollaboration;
import music_service.model.ArtistEls;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ArtistElsMapper {

    ArtistElsMapper INSTANCE = Mappers.getMapper(ArtistElsMapper.class);

    // Mapping individual ArtistEls to ArtistGeneralResponse
    ArtistGeneralResponse toArtistGeneralResponse(ArtistEls artistEls);

    // Mapping List<ArtistEls> to List<ArtistGeneralResponse>
    List<ArtistGeneralResponse> toArtistGeneralResponseList(List<ArtistEls> artistEls);

    // Mapping Iterable<ArtistEls> to List<ArtistGeneralResponse>
    List<ArtistGeneralResponse> toArtistGeneralResponseListFromIterable(Iterable<ArtistEls> artistEls);

}
