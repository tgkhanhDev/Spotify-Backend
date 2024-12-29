package api_gateway.service;

import api_gateway.mapper.MusicMapper;
import api_gateway.model.Music;
import api_gateway.dto.musicDto.response.MusicResponse;
import api_gateway.repository.MusicRepository;
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
