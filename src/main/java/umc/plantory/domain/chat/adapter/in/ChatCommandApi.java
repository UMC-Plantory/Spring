package umc.plantory.domain.chat.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import umc.plantory.domain.chat.adapter.in.dto.ChatReqDto;
import umc.plantory.global.apiPayload.ApiResponse;

import java.security.Principal;

public interface ChatCommandApi {
    @Operation(summary = "채팅 요청 API", description = "챗봇 요청 API 입니다.")
    ResponseEntity<ApiResponse<String>> chat(@RequestBody @Valid ChatReqDto.ChatRequest request,@RequestParam("userId") Long userId);
}
