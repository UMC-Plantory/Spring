package umc.plantory.domain.chat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.plantory.domain.chat.controller.dto.ChatReqDto;
import umc.plantory.domain.chat.service.ChatCommandUseCase;
import umc.plantory.global.apiPayload.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatCommandController implements ChatCommandApi {
    private final ChatCommandUseCase chatCommandUseCase;
    /**
     * 채팅 요청을 받아 챗봇 응답을 반환합니다.
     * @param request 채팅 요청 DTO
     * @param memberId 회원 ID
     * @return 챗봇 응답
     */
    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<String>> chat(@RequestBody @Valid ChatReqDto.ChatRequest request, @RequestParam("memberId") Long memberId) {
        String response = chatCommandUseCase.ask(request.content(), memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
