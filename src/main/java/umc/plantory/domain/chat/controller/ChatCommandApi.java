package umc.plantory.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import umc.plantory.domain.chat.controller.dto.ChatRequestDto;
import umc.plantory.global.apiPayload.ApiResponse;

public interface ChatCommandApi {
    @Operation(
        summary = "챗봇 채팅 요청",
        description = "사용자가 챗봇에게 메시지를 보내면, 챗봇의 답변을 반환합니다. " +
                      "소셜로그인 구현 이전이므로 memberId는 쿼리 파라미터로, content는 JSON body로 전달"
    )
    ResponseEntity<ApiResponse<String>> chat(
       @RequestBody @Valid ChatRequestDto.ChatRequest request,
        @Parameter(
            description = "회원 ID",
            example = "1"
        ) @RequestParam Long memberId
    );
}
