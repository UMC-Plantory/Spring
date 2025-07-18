package umc.plantory.domain.chat.service;

import umc.plantory.domain.chat.controller.dto.ChatResDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatQueryUseCase {
    List<ChatResDto.ChatResponse> findLatestChatsByMemberId(Long memberId);
    List<ChatResDto.ChatResponse> findBeforeChatsByMemberId(Long memberid, LocalDateTime before);
}
