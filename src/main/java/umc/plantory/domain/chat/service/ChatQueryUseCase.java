package umc.plantory.domain.chat.service;

import umc.plantory.domain.chat.controller.dto.ChatResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatQueryUseCase {
    List<ChatResponseDto.ChatResponse> findLatestChatsByMemberId(Long memberId);
    List<ChatResponseDto.ChatResponse> findBeforeChatsByMemberId(Long memberid, LocalDateTime before);
}
