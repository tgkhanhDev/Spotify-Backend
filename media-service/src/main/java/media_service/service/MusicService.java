package media_service.service;

import media_service.dto.musicDto.response.MusicResponse;

import java.util.List;

public interface MusicService {
    List<MusicResponse> getAllMusic();
}
