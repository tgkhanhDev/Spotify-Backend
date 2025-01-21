package music_service.service;

import music_service.dto.musicDto.request.MusicRequest;
import music_service.dto.musicDto.response.MusicResponse;
import music_service.mapper.MusicElsMapper;
import music_service.mapper.MusicMapper;
import music_service.repository.CustomMusicElsRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MusicServiceImpl implements MusicService {

    CustomMusicElsRepository musicElsRepository;
    MusicMapper musicMapper;
    MusicElsMapper musicElsMapper;

    @Autowired
    public MusicServiceImpl(CustomMusicElsRepository musicElsRepository, MusicMapper musicMapper, MusicElsMapper musicElsMapper) {
        this.musicElsRepository = musicElsRepository;
        this.musicMapper = musicMapper;
        this.musicElsMapper = musicElsMapper;
    }

    @Override
    public List<MusicResponse> getAllMusic(Jwt jwtToken, String searchText) {
        if (searchText == null) {
            searchText = "";
        }
        if (jwtToken == null) {
//            throw new AuthenException(ErrorCode.INVALID_TOKEN);
            return musicElsMapper.toMusicResponseList(musicElsRepository.findWithFilter(searchText));
        }

        return musicElsMapper.toMusicResponseList(musicElsRepository.findWithFilter(searchText));
    }

    @Override
    public List<MusicResponse> addMusic(Jwt jwtToken, MusicRequest musicRequest) {

        return null;
    }
}
