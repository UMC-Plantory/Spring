package umc.plantory.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.plantory.domain.chat.dto.ChatRequestDTO;
import umc.plantory.domain.chat.service.ChatCommandUseCase;
import umc.plantory.global.apiPayload.ApiResponse;

/** 심현민 & 박형진 공동 작업 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/plantory/chat")
public class ChatCommandController {
    private final ChatCommandUseCase chatCommandUseCase;

    @PostMapping
    @Operation(
            summary = "챗봇 채팅 요청",
            description = "사용자가 챗봇에게 메시지를 보내면, 챗봇의 답변을 반환합니다."
    )
    public ResponseEntity<ApiResponse<String>> chat(
            @RequestBody @Valid ChatRequestDTO.ChatMessageDTO request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        String response = chatCommandUseCase.ask(authorization, request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
