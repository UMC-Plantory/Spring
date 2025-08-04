package umc.plantory.domain.chat.service;

import umc.plantory.domain.chat.dto.ChatResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatQueryUseCase {
    List<ChatResponseDTO.ChatResponse> findLatestChats(String authorization);
    List<ChatResponseDTO.ChatResponse> findBeforeChats(String authorization, LocalDateTime before);
}
