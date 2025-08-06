package umc.plantory.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.plantory.domain.chat.dto.ChatRequestDTO;
import umc.plantory.domain.chat.dto.ChatResponseDTO;
import umc.plantory.domain.chat.service.ChatCommandUseCase;
import umc.plantory.domain.chat.service.ChatQueryUseCase;
import umc.plantory.global.apiPayload.ApiResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** 심현민 & 박형진 공동 작업 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/plantory/chat")
@Tag(name = "Chat", description = "채팅 관련 API")
public class ChatRestController {

    private final ChatQueryUseCase chatQueryUseCase;
    private final ChatCommandUseCase chatCommandUseCase;

    @PostMapping
    @Operation(
            summary = "챗봇 채팅 요청",
            description = "사용자가 챗봇에게 메시지를 보내면, 챗봇의 답변을 반환합니다."
    )
    public ResponseEntity<ApiResponse<String>> chat (
            @RequestBody @Valid ChatRequestDTO.ChatMessageDTO request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        String response = chatCommandUseCase.ask(authorization, request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @GetMapping("/list")
    @Operation(
            summary = "최초 진입 이후, 챗봇 채팅창 이전 채팅 스크롤 조회",
            description = "스크롤 시, 기준 시각(cursor) 이전의 채팅을 추가로 조회 "
    )
    public ResponseEntity<ApiResponse<ChatResponseDTO.ChatsResponse>> getChats(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(value = "cursor", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime cursor,
            @RequestParam(value = "size", defaultValue = "6") int size
    ) {
        ChatResponseDTO.ChatsResponse chatList = chatQueryUseCase.findChatList(authorization, cursor, size);
        return ResponseEntity.ok(ApiResponse.onSuccess(chatList));
    }
}
