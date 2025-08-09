package umc.plantory.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.plantory.domain.chat.dto.ChatRequestDTO;
import umc.plantory.domain.chat.dto.ChatResponseDTO;
import umc.plantory.domain.chat.service.ChatCommandUseCase;
import umc.plantory.domain.chat.service.ChatQueryUseCase;
import umc.plantory.global.apiPayload.ApiResponse;

import java.time.LocalDateTime;
import java.util.List;

/** 심현민 & 박형진 공동 작업 **/
@Tag(name = "Chat", description = "채팅 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/plantory/chat")
public class ChatRestController {

    private final ChatQueryUseCase chatQueryUseCase;
    private final ChatCommandUseCase chatCommandUseCase;

    @Operation(
            summary = "챗봇 채팅 요청",
            description = "사용자가 챗봇에게 메시지를 보내면, 챗봇의 답변을 반환합니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<ChatResponseDTO>> chat (
            @RequestBody @Valid ChatRequestDTO request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        ChatResponseDTO response = chatCommandUseCase.ask(authorization, request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(
            summary = "챗봇 채팅창 이전 대화 기록 최초 조회",
            description = "사용자가 챗봇과 나눈 이전 대화 중 가장 최근 6개를 조회합니다. "
    )
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<List<ChatResponseDTO>>> getChatHistoryLatest (
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        List<ChatResponseDTO> latestChats = chatQueryUseCase.findLatestChats(authorization);
        return ResponseEntity.ok(ApiResponse.onSuccess(latestChats));
    }

    @Operation(
            summary = "최초 진입 이후, 챗봇 채팅창 이전 채팅 스크롤 조회",
            description = "스크롤 시, 기준 시각(before) 이전의 6개 채팅을 추가로 조회 "
    )
    @GetMapping("/before")
    public ResponseEntity<ApiResponse<List<ChatResponseDTO>>> getChatHistoryBefore (
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam("before") LocalDateTime before
    ) {
        List<ChatResponseDTO> beforeChats = chatQueryUseCase.findBeforeChats(authorization, before);
        return ResponseEntity.ok(ApiResponse.onSuccess(beforeChats));
    }
}
