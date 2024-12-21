package music_service.service;

import music_service.dto.musicDto.response.MusicResponse;
import music_service.mapper.MusicMapper;
import music_service.model.Music;
import music_service.repository.MusicRepository;
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
