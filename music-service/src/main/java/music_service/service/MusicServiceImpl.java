package music_service.service;

import music_service.dto.musicDto.response.MusicResponse;
import music_service.mapper.MusicMapper;
import music_service.model.Music;
import music_service.model.MusicEls;
import music_service.repository.MusicElsRepository;
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
    MusicElsRepository musicElsRepository;

    MusicMapper musicMapper;

    public MusicServiceImpl(MusicRepository musicRepository, MusicElsRepository musicElsRepository, MusicMapper musicMapper) {
        this.musicRepository = musicRepository;
        this.musicElsRepository = musicElsRepository;
        this.musicMapper = musicMapper;
    }

    @Override
    public List<MusicResponse> getAllMusic() {
        List<Music> music = musicRepository.findAll();
        System.out.println("Here:");
        Iterable<MusicEls> test = musicElsRepository.findAll();


        return musicMapper.toMusicResponseList(music);
    }
}
