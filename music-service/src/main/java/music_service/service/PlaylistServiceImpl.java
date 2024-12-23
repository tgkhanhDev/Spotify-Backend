package music_service.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import music_service.dto.playlistDto.request.UpdatePlaylistRequest;
import music_service.dto.playlistDto.response.PlaylistOverallResponse;
import music_service.dto.playlistDto.response.PlaylistResponse;
import music_service.exception.AuthenException;
import music_service.exception.ErrorCode;
import music_service.exception.MusicException;
import music_service.mapper.PlaylistMapper;
import music_service.model.Account;
import music_service.model.Playlist;
import music_service.repository.AccountRepository;
import music_service.repository.PlaylistRepository;
import music_service.repository.SavedPlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlaylistServiceImpl implements PlaylistService {

    PlaylistRepository playlistRepository;
    SavedPlaylistRepository savedPlaylistRepository;
    AccountRepository accountRepository;
    PlaylistMapper playlistMapper;

    @Autowired
    public PlaylistServiceImpl(PlaylistRepository playlistRepository, SavedPlaylistRepository savedPlaylistRepository, AccountRepository accountRepository, PlaylistMapper playlistMapper) {
        this.playlistRepository = playlistRepository;
        this.savedPlaylistRepository = savedPlaylistRepository;
        this.accountRepository = accountRepository;
        this.playlistMapper = playlistMapper;
    }


    @Override
    public List<PlaylistOverallResponse> getAllUserPlaylist() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenException(ErrorCode.UNAUTHORIZED);
        }

        // Get JWT token details
        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
//            log.info("JWT Token: {}", jwt.getClaim("sub").toString() );
            String userId = jwt.getClaim("userId"); // Replace "sub" with the appropriate claim key for user ID
            List<Playlist> playlists = playlistRepository.findAllByAccount_Id(UUID.fromString(userId),Sort.by(Sort.Direction.DESC, "createdTime"));

            return playlistMapper.toPlaylistOverallResponseList(playlists);
        } else {
            throw new AuthenException(ErrorCode.CLAIM_NOT_FOUND);
        }
    }

    @Override
    public PlaylistResponse getPlaylistById(UUID playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId).orElse(null);
        if (playlist != null) {
            return playlistMapper.toPlaylistResponse(playlist);
        }
        return null;
    }

    @Override
    @Transactional
    public PlaylistResponse createPlaylist() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenException(ErrorCode.UNAUTHORIZED);
        }

        // Get JWT token details
        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String userId = jwt.getClaim("userId");

            long count = playlistRepository.countByAccount_Id(UUID.fromString(userId));
            Account account = accountRepository.findById(UUID.fromString(userId)).orElse(null);
            if(account == null)
                throw new AuthenException(ErrorCode.USER_NOT_EXISTED);

            Playlist playlist = new Playlist().builder()
                    .title("My Playlist #" + (count))
                    .createdTime(LocalDateTime.now())
                    .account(account)
                    .build();

            return playlistMapper.toPlaylistResponse(playlistRepository.save(playlist));

        } else {
            throw new AuthenException(ErrorCode.CLAIM_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public PlaylistResponse deletePlaylistById(UUID playlistId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenException(ErrorCode.UNAUTHORIZED);
        }

        // Get JWT token details
        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String userId = jwt.getClaim("userId");

            Playlist playlistDeleted = playlistRepository.findById(playlistId).orElse(null);

            if (playlistDeleted == null) {
                throw new MusicException(ErrorCode.PLAYLIST_NOT_FOUND);
            }

            //Check permission
            if(!Objects.equals(userId, playlistDeleted.getAccount().getId().toString())){
                throw new AuthenException(ErrorCode.USER_DOES_NOT_HAVE_PERMISSION);
            }

            //remove constraint
//            playlistDeleted.setAccount(null);
//            playlistRepository.save(playlistDeleted);

            //! Delete nho check table M - M
            playlistRepository.deleteById(playlistId);

            return playlistMapper.toPlaylistResponse(playlistDeleted);
        } else {
            throw new AuthenException(ErrorCode.CLAIM_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public PlaylistResponse updatePlaylistInfo(UpdatePlaylistRequest playlist) {
        Playlist playlistUpdated = playlistRepository.findById(playlist.getPlaylistId()).orElse(null);
        if (playlistUpdated == null) {
            throw new MusicException(ErrorCode.PLAYLIST_NOT_FOUND);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenException(ErrorCode.UNAUTHORIZED);
        }

        // Get JWT token details
        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String userId = jwt.getClaim("userId");
            if(!Objects.equals(userId, playlistUpdated.getAccount().getId().toString())){
                throw new AuthenException(ErrorCode.USER_DOES_NOT_HAVE_PERMISSION);
            }
        }

        if (playlist.getTitle() != null) {
            playlistUpdated.setTitle(playlist.getTitle());
        }
        if (playlist.getDescription() != null) {
            playlistUpdated.setDescription(playlist.getDescription());
        }
        if (playlist.getBackgroundImage() != null) {
            playlistUpdated.setBackgroundImage(playlist.getBackgroundImage());
        }

        playlistRepository.save(playlistUpdated);
        return playlistMapper.toPlaylistResponse(playlistUpdated);
    }

}
