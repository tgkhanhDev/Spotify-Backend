package auth_service.service;

import auth_service.mapper.MusicMapper;
import auth_service.model.Music;
import auth_service.repository.MusicRepository;
import auth_service.dto.musicDto.response.MusicResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MusicServiceImpl implements MusicService {

    MusicRepository musicRepository;


    MusicMapper musicMapper;

    public MusicServiceImpl(MusicRepository musicRepository, MusicMapper musicMapper) {
        this.musicRepository = musicRepository;
        this.musicMapper = musicMapper;
    }

    @Override
    public List<MusicResponse> getAllMusic() {
        List<Music> music = musicRepository.findAll();
        return musicMapper.toMusicResponseList(music);
    }
}
