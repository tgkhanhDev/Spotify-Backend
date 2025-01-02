package auth_service.service;

import auth_service.dto.musicDto.response.MusicResponse;

import java.util.List;

public interface MusicService {
    List<MusicResponse> getAllMusic();
}
