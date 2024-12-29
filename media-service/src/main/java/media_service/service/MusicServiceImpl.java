package media_service.service;

import media_service.dto.musicDto.response.MusicResponse;
import media_service.mapper.MusicMapper;
import media_service.model.Music;
import media_service.repository.MusicRepository;
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
