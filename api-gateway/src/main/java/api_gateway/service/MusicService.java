package api_gateway.service;

import api_gateway.dto.musicDto.response.MusicResponse;

import java.util.List;

public interface MusicService {
    List<MusicResponse> getAllMusic();
}
