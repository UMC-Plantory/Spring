package umc.plantory.domain.chat.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.plantory.domain.chat.adapter.out.dto.ChatResDto;
import umc.plantory.domain.chat.port.in.ChatQueryUseCase;
import umc.plantory.global.apiPayload.ApiResponse;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatQueryController implements ChatQueryApi {
    private final ChatQueryUseCase chatQueryUseCase;

    // 챗봇 채팅창 처음 최초 진입 : 최신 6개
    @Override
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<List<ChatResDto.ChatResponse>>> getChatHistoryLatest(@RequestParam("memberId") Long memberId) {
        List<ChatResDto.ChatResponse> latestChats = chatQueryUseCase.findLatestChatsByMemberId(memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(latestChats));
    }
    // 최초 진입 이후, 스크롤 업: 특정 createdAt 이전 6개 조회 (커서 페이징)
    @Override
    @GetMapping("/before")
    public ResponseEntity<ApiResponse<List<ChatResDto.ChatResponse>>> getChatHistoryBefore(@RequestParam("memberId") Long memberId, @RequestParam("before") LocalDateTime before) {
        List<ChatResDto.ChatResponse> beforeChats = chatQueryUseCase.findBeforeChatsByMemberId(memberId, before);
        return ResponseEntity.ok(ApiResponse.onSuccess(beforeChats));
    }
}
