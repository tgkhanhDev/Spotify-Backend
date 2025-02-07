package music_service.mapper;

import music_service.dto.artistCollaborationDto.response.ArtistCollaborationResponse;
import music_service.dto.authenticationDto.response.AccountArtistResponse;
import music_service.model.ArtistCollaboration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = AccountMapper.class)
public interface ArtistCollaborationMapper {
    ArtistCollaborationResponse toArtistCollaborationResponse(ArtistCollaboration artistCollaboration);
    List<ArtistCollaborationResponse> toArtistCollaborationResponseList(List<ArtistCollaboration> artistCollaboration);

    AccountArtistResponse toAccountArtistResponse(ArtistCollaborationResponse artistCollaborationResponse);
}
