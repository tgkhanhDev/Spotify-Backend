package auth_service.mapper;

import auth_service.model.ArtistCollaboration;
import auth_service.dto.artistCollaborationDto.response.ArtistCollaborationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = AccountMapper.class)
public interface ArtistCollaborationMapper {
    ArtistCollaborationResponse toArtistCollaborationResponse(ArtistCollaboration artistCollaboration);
    List<ArtistCollaborationResponse> toArtistCollaborationResponseList(List<ArtistCollaboration> artistCollaboration);

}
