package music_service.service;

import music_service.dto.musicDto.response.MusicResponse;

import java.util.List;

public interface MusicService {
    List<MusicResponse> getAllMusic();
}
