package umc.plantory.domain.chat.port.in;

import umc.plantory.domain.chat.adapter.out.dto.ChatResDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatQueryUseCase {
    List<ChatResDto.ChatResponse> findLatestChatsByMemberId(Long memberId);
    List<ChatResDto.ChatResponse> findBeforeChatsByMemberId(Long memberid, LocalDateTime before);
}
