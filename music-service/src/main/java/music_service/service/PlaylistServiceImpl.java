package music_service.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import music_service.config.JWT.CustomJwtDecoder;
import music_service.dto.authenticationDto.response.AccountResponse;
import music_service.dto.playlistDto.request.UpdatePlaylistMusicRequest;
import music_service.dto.playlistDto.request.UpdatePlaylistRequest;
import music_service.dto.playlistDto.response.PlaylistDetailByUser;
import music_service.dto.playlistDto.response.PlaylistOverallResponse;
import music_service.dto.playlistDto.response.PlaylistResponse;
import music_service.exception.AuthenException;
import music_service.exception.ErrorCode;
import music_service.exception.MusicException;
import music_service.mapper.PlaylistMapper;
import music_service.model.Account;
import music_service.model.Playlist;
import music_service.model.PlaylistMusic;
import music_service.model.embedKeys.PlaylistMusicKey;
import music_service.producer.UserProducer;
import music_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContext;
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
    CustomJwtDecoder customJwtDecoder;
    UserProducer userProducer;

    @Autowired
    public PlaylistServiceImpl(PlaylistRepository playlistRepository, SavedPlaylistRepository savedPlaylistRepository, AccountRepository accountRepository, MusicRepository musicRepository, PlaylistMusicRepository playlistMusicRepository, PlaylistMapper playlistMapper, CustomJwtDecoder customJwtDecoder, UserProducer userProducer) {
        this.playlistRepository = playlistRepository;
        this.savedPlaylistRepository = savedPlaylistRepository;
        this.accountRepository = accountRepository;
        this.musicRepository = musicRepository;
        this.playlistMusicRepository = playlistMusicRepository;
        this.playlistMapper = playlistMapper;
        this.customJwtDecoder = customJwtDecoder;
        this.userProducer = userProducer;
    }


    @Override
    public List<PlaylistOverallResponse> getAllPlaylist() {
        return playlistRepository.findAll(Sort.by(Sort.Direction.DESC, "createdTime")).stream().map(playlistMapper::toPlaylistOverallResponse).toList();
    }

    @Override
    public List<PlaylistOverallResponse> getAllUserPlaylist(Jwt jwtToken) {
        UUID userIdClaims = UUID.fromString(jwtToken.getClaim("userId")); // Replace "sub" with the appropriate claim key for user ID
        System.out.println("JWT token: " + jwtToken);
        List<Playlist> playlists = playlistRepository.findAllByAccount_Id(userIdClaims, Sort.by(Sort.Direction.DESC, "createdTime"));

        return playlistMapper.toPlaylistOverallResponseList(playlists);

    }

    @Override
    public PlaylistResponse getPlaylistById(UUID playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() -> new MusicException(ErrorCode.PLAYLIST_NOT_FOUND));
        return playlistMapper.toPlaylistResponse(playlist);
    }

    @Override
    @Transactional
    public PlaylistResponse createPlaylist(Jwt jwtToken) {
        UUID userIdClaims = UUID.fromString(jwtToken.getClaim("userId")); // Replace "sub" with the appropriate claim key for user ID

        long count = playlistRepository.countByAccount_Id(userIdClaims);
        Account account = accountRepository.findById(userIdClaims).orElseThrow(() -> new AuthenException(ErrorCode.USER_NOT_EXISTED));

        Playlist playlist = new Playlist().builder()
                .title("My Playlist #" + (count))
                .createdTime(LocalDateTime.now())
                .account(account)
                .playlistMusicSet(new ArrayList<>())
                .build();

        return playlistMapper.toPlaylistResponse(playlistRepository.save(playlist));
    }

    @Override
    @Transactional
    public PlaylistResponse deletePlaylistById(UUID playlistId, Jwt jwtToken) {
        //Decode JWT
        String userIdClaims = jwtToken.getClaim("userId"); // Replace "sub" with the appropriate claim key for user ID

        //Check permission
        Playlist playlistDeleted = playlistRepository.findById(playlistId).orElseThrow(() -> new MusicException(ErrorCode.PLAYLIST_NOT_FOUND));
        if (!Objects.equals(userIdClaims, playlistDeleted.getAccount().getId().toString())) {
            throw new AuthenException(ErrorCode.USER_DOES_NOT_HAVE_PERMISSION);
        }

        playlistRepository.deleteById(playlistId);
        return playlistMapper.toPlaylistResponse(playlistDeleted);
    }

    @Override
    @Transactional
    public PlaylistResponse updatePlaylistInfo(UpdatePlaylistRequest playlist, Jwt jwtToken) {
        String userIdClaims = jwtToken.getClaim("userId"); // Replace "sub" with the appropriate claim key for user ID

        Playlist playlistUpdated = playlistRepository.findById(playlist.getPlaylistId()).orElseThrow(() -> new MusicException(ErrorCode.PLAYLIST_NOT_FOUND));

        if (!Objects.equals(userIdClaims, playlistUpdated.getAccount().getId().toString())) {
            throw new AuthenException(ErrorCode.USER_DOES_NOT_HAVE_PERMISSION);
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
    public PlaylistResponse addPlaylistMusic(UpdatePlaylistMusicRequest request, Jwt jwtToken) {
        String userIdClaims = jwtToken.getClaim("userId"); // Replace "sub" with the appropriate claim key for user ID

        Playlist playlistUpdated = playlistRepository.findById(request.getPlaylistId()).orElseThrow(() -> new MusicException(ErrorCode.PLAYLIST_NOT_FOUND));

        if (!Objects.equals(userIdClaims, playlistUpdated.getAccount().getId().toString())) {
            throw new AuthenException(ErrorCode.USER_DOES_NOT_HAVE_PERMISSION);
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
    public PlaylistResponse removePlaylistMusic(UpdatePlaylistMusicRequest request, Jwt jwtToken) {
        String userIdClaims = jwtToken.getClaim("userId"); // Replace "sub" with the appropriate claim key for user ID

        Playlist playlistUpdated = playlistRepository.findById(request.getPlaylistId()).orElseThrow(() -> new MusicException(ErrorCode.PLAYLIST_NOT_FOUND));

        if (!Objects.equals(userIdClaims, playlistUpdated.getAccount().getId().toString())) {
            throw new AuthenException(ErrorCode.USER_DOES_NOT_HAVE_PERMISSION);
        }

        //Is music already in playlist?
        boolean isExist = playlistMusicRepository.findByMusicIdAndPlaylistId(request.getMusicId(), request.getPlaylistId())
                .isPresent();

        if (!isExist) {
            throw new MusicException(ErrorCode.MUSIC_NOT_FOUND_IN_PLAYLIST);
        }

        //Remove music from playlist
        playlistMusicRepository.deleteByMusicIdAndPlaylistId(request.getMusicId(), request.getPlaylistId());

        return playlistMapper.toPlaylistResponse(playlistRepository.findById(request.getPlaylistId()).get());
    }


}
