package auth_service.service;

import auth_service.exception.AuthenException;
import auth_service.exception.ErrorCode;
import auth_service.exception.MusicException;
import auth_service.mapper.PlaylistMapper;
import auth_service.model.Account;
import auth_service.model.Playlist;
import auth_service.model.PlaylistMusic;
import auth_service.model.embedKeys.PlaylistMusicKey;
import auth_service.repository.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import auth_service.dto.playlistDto.request.UpdatePlaylistMusicRequest;
import auth_service.dto.playlistDto.request.UpdatePlaylistRequest;
import auth_service.dto.playlistDto.response.PlaylistOverallResponse;
import auth_service.dto.playlistDto.response.PlaylistResponse;
import auth_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlaylistServiceImpl implements PlaylistService {

    PlaylistRepository playlistRepository;
    SavedPlaylistRepository savedPlaylistRepository;
    AccountRepository accountRepository;
    MusicRepository musicRepository;
    PlaylistMusicRepository playlistMusicRepository;

    PlaylistMapper playlistMapper;

    @Autowired
    public PlaylistServiceImpl(PlaylistRepository playlistRepository, SavedPlaylistRepository savedPlaylistRepository, AccountRepository accountRepository, MusicRepository musicRepository, PlaylistMusicRepository playlistMusicRepository, PlaylistMapper playlistMapper) {
        this.playlistRepository = playlistRepository;
        this.savedPlaylistRepository = savedPlaylistRepository;
        this.accountRepository = accountRepository;
        this.musicRepository = musicRepository;
        this.playlistMusicRepository = playlistMusicRepository;
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
            List<Playlist> playlists = playlistRepository.findAllByAccount_Id(UUID.fromString(userId), Sort.by(Sort.Direction.DESC, "createdTime"));

            return playlistMapper.toPlaylistOverallResponseList(playlists);
        } else {
            throw new AuthenException(ErrorCode.CLAIM_NOT_FOUND);
        }
    }

    @Override
    public PlaylistResponse getPlaylistById(UUID playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() -> new MusicException(ErrorCode.PLAYLIST_NOT_FOUND));
        return playlistMapper.toPlaylistResponse(playlist);
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
            Account account = accountRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new AuthenException(ErrorCode.USER_NOT_EXISTED));

            Playlist playlist = new Playlist().builder()
                    .title("My Playlist #" + (count))
                    .createdTime(LocalDateTime.now())
                    .account(account)
                    .playlistMusicSet(new ArrayList<>())
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

            //Check permission
            Playlist playlistDeleted = playlistRepository.findById(playlistId).orElseThrow(() -> new MusicException(ErrorCode.PLAYLIST_NOT_FOUND));
            if (!Objects.equals(userId, playlistDeleted.getAccount().getId().toString())) {
                throw new AuthenException(ErrorCode.USER_DOES_NOT_HAVE_PERMISSION);
            }

            playlistRepository.deleteById(playlistId);

            return playlistMapper.toPlaylistResponse(playlistDeleted);
        } else {
            throw new AuthenException(ErrorCode.CLAIM_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public PlaylistResponse updatePlaylistInfo(UpdatePlaylistRequest playlist) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenException(ErrorCode.UNAUTHORIZED);
        }

        // Get JWT token details
        Playlist playlistUpdated = playlistRepository.findById(playlist.getPlaylistId()).orElseThrow(() -> new MusicException(ErrorCode.PLAYLIST_NOT_FOUND));
        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String userId = jwt.getClaim("userId");
            if (!Objects.equals(userId, playlistUpdated.getAccount().getId().toString())) {
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

    @Override
    @Transactional
    public PlaylistResponse addPlaylistMusic(UpdatePlaylistMusicRequest request) {

        //Check Authen
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenException(ErrorCode.UNAUTHORIZED);
        }

        //Check permission of user
        Playlist playlistUpdated = playlistRepository.findById(request.getPlaylistId()).orElseThrow(() -> new MusicException(ErrorCode.PLAYLIST_NOT_FOUND));
        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String userId = jwt.getClaim("userId");
            if (!Objects.equals(userId, playlistUpdated.getAccount().getId().toString())) {
                throw new AuthenException(ErrorCode.USER_DOES_NOT_HAVE_PERMISSION);
            }
        }

        //Is music already in playlist?
        playlistMusicRepository.findByMusicIdAndPlaylistId(request.getMusicId(), request.getPlaylistId()).ifPresent(
                playlistMusic -> {
                    throw new MusicException(ErrorCode.MUSIC_ALREADY_IN_PLAYLIST);
                }
        );

        //Add music to playlistMusic
        PlaylistMusic playlistMusic = playlistMusicRepository.save(PlaylistMusic.builder()
                        .id(new PlaylistMusicKey(request.getPlaylistId(), request.getMusicId()))
                        .music(musicRepository.findById(request.getMusicId()).orElseThrow(() -> new MusicException(ErrorCode.MUSIC_NOT_FOUND)))
                        .playlist(playlistUpdated)
                        .addTime(LocalDateTime.now())
                .build());

        //Add playlistMusic to playlist
        playlistUpdated.getPlaylistMusicSet().add(playlistMusic);

        return playlistMapper.toPlaylistResponse(playlistRepository.save(playlistUpdated));
    }

    @Override
    @Transactional
    public PlaylistResponse removePlaylistMusic(UpdatePlaylistMusicRequest request) {
        //Check Authen
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenException(ErrorCode.UNAUTHORIZED);
        }

        //Check permission of user
        Playlist playlistUpdated = playlistRepository.findById(request.getPlaylistId()).orElseThrow(() -> new MusicException(ErrorCode.PLAYLIST_NOT_FOUND));
        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String userId = jwt.getClaim("userId");
            if (!Objects.equals(userId, playlistUpdated.getAccount().getId().toString())) {
                throw new AuthenException(ErrorCode.USER_DOES_NOT_HAVE_PERMISSION);
            }
        }

        //Is music already in playlist?
        boolean isExist = playlistMusicRepository.findByMusicIdAndPlaylistId(request.getMusicId(), request.getPlaylistId())
                .isPresent();

        if (!isExist) {
            throw new MusicException(ErrorCode.MUSIC_NOT_FOUND_IN_PLAYLIST);
        }

        //Remove music from playlist
        playlistMusicRepository.deleteByMusicIdAndPlaylistId(request.getMusicId(), request.getPlaylistId());

        return playlistMapper.toPlaylistResponse( playlistRepository.findById(request.getPlaylistId()).get() );
    }


}
