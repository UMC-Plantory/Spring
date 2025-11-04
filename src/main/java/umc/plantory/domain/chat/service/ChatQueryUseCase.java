package umc.plantory.domain.chat.service;

import umc.plantory.domain.chat.dto.ChatResponseDTO;

import java.time.LocalDateTime;

public interface ChatQueryUseCase {
    ChatResponseDTO.ChatsResponse findChatList(String authorization, LocalDateTime cursor, int size);
    ChatResponseDTO.ChatIdsResponse searchChatIdsByKeyword(String authorization, String keyword);
}
