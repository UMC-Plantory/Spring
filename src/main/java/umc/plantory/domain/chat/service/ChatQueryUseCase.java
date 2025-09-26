package umc.plantory.domain.chat.service;

import umc.plantory.domain.chat.dto.ChatResponseDTO;

import java.time.LocalDateTime;

public interface ChatQueryUseCase {
    ChatResponseDTO.ChatsResponse findChatList(String authorization, LocalDateTime cursor, int size);
    ChatResponseDTO.ChatsIdListResponse getChatsByKeyword(String authorization, String keyword);
}
