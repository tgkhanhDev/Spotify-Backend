package music_service.service;

import jakarta.transaction.Transactional;
import music_service.dto.artistCollaborationDto.response.ArtistCollaborationResponse;
import music_service.dto.authenticationDto.response.AccountArtistResponse;
import music_service.dto.musicDto.request.MusicRequest;
import music_service.dto.musicDto.response.MusicResponse;
import music_service.exception.AuthenException;
import music_service.exception.ErrorCode;
import music_service.mapper.MusicElsMapper;
import music_service.mapper.MusicMapper;
import music_service.model.Account;
import music_service.model.ArtistCollaboration;
import music_service.model.Music;
import music_service.model.MusicEls;
import music_service.model.embedKeys.ArtistCollabKey;
import music_service.repository.AccountRepository;
import music_service.repository.ArtistCollaborationRepository;
import music_service.repository.CustomMusicElsRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import music_service.repository.MusicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MusicServiceImpl implements MusicService {

    CustomMusicElsRepository musicElsRepository;
    MusicRepository musicRepository;
    ArtistCollaborationRepository artistCollaborationRepository;
    AccountRepository accountRepository;
    MusicMapper musicMapper;
    MusicElsMapper musicElsMapper;

    @Autowired
    public MusicServiceImpl(CustomMusicElsRepository musicElsRepository, MusicRepository musicRepository, ArtistCollaborationRepository artistCollaborationRepository, AccountRepository accountRepository, MusicMapper musicMapper, MusicElsMapper musicElsMapper) {
        this.musicElsRepository = musicElsRepository;
        this.musicRepository = musicRepository;
        this.artistCollaborationRepository = artistCollaborationRepository;
        this.accountRepository = accountRepository;
        this.musicMapper = musicMapper;
        this.musicElsMapper = musicElsMapper;
    }

    @Override
    public List<MusicResponse> getAllMusic(Jwt jwtToken, String searchText) {
        if (searchText == null) {
            searchText = "";
        }
        if (jwtToken == null) {
            return musicElsMapper.toMusicResponseList(musicElsRepository.findWithFilter(searchText));
        }
        return musicElsMapper.toMusicResponseList(musicElsRepository.findWithFilter(searchText));
    }

    @Override
    public List<MusicResponse> getAllMusicByArtist(String artistId) {
        return musicElsMapper.toMusicResponseList(musicElsRepository.findByArtist(artistId));
    }

    @Override
    public MusicResponse deleteMusic(Jwt jwtToken, String musicId) {

        //! Dang ly cho nay phai check jwt's userId co khop voi music hay khong. Nhung ma toi luoi qua =)))
        Boolean isSubscribe = jwtToken.getClaim("isSubscribe"); // Replace "sub" with the appropriate claim key for user ID
        if (!isSubscribe) throw new AuthenException(ErrorCode.USER_NOT_SUBSCRIBED);

        MusicResponse music = musicMapper.toMusicResponse(musicRepository.findById(UUID.fromString(musicId)).orElseThrow(() -> new AuthenException(ErrorCode.MUSIC_NOT_FOUND)));
        musicRepository.deleteById(UUID.fromString(musicId));
        musicElsRepository.deleteMusicEls(musicId);
        return music;
    }

    @Override
    @Transactional
    public MusicResponse addMusic(Jwt jwtToken, MusicRequest musicRequest) {
        Boolean isSubscribe = jwtToken.getClaim("isSubscribe"); // Replace "sub" with the appropriate claim key for user ID

        if (!isSubscribe) throw new AuthenException(ErrorCode.USER_NOT_SUBSCRIBED);

        Account artist = accountRepository.findById(UUID.fromString(jwtToken.getClaim("userId"))).orElseThrow(() -> new AuthenException(ErrorCode.USER_NOT_EXISTED));

        Music music = Music
                .builder()
                .id(UUID.randomUUID())
                .musicName(musicRequest.getMusicName())
                .musicUrl(musicRequest.getMusicUrl())
                .thumbnail(musicRequest.getThumbnail())
                .uploadTime(LocalDateTime.parse(musicRequest.getUploadTime()))
                .build();
        Music savedMusic = musicRepository.save(music);

        ArtistCollaboration ac = ArtistCollaboration.builder()
                .accountId(artist)
                .id(ArtistCollabKey.builder()
                        .accountId(UUID.fromString(jwtToken.getClaim("userId")))
                        .musicId(savedMusic.getId())
                        .build()
                )
                .thumbnail("")
                .build();
        artistCollaborationRepository.save(ac);

//        updateList AC to Music
        music.setArtistCollaboration(List.of(ac));
        Music updatedMusic = musicRepository.save(music);

        ArtistCollaborationResponse acr = ArtistCollaborationResponse
                .builder()
                .account(AccountArtistResponse.builder()
                        .id(artist.getId())
                        .nickName(artist.getNickName())
                        .build())
                .thumbnail(ac.getThumbnail())
                .build();

        MusicEls musicEls = MusicEls.builder()
                .version("1")
                .timestamp(LocalDateTime.now().toString())
                .type("sync_music")
                .id(music.getId().toString())
                .musicName(musicRequest.getMusicName())
                .musicUrl(musicRequest.getMusicUrl())
                .uploadTime(musicRequest.getUploadTime())
                .thumbnail(musicRequest.getThumbnail())
                .musicUrl(musicRequest.getMusicUrl())
                .artistCollaboration(List.of(acr))
                .build();

        musicElsRepository.saveMusicEls(musicEls);
        return musicMapper.toMusicResponse(music);
    }
}
