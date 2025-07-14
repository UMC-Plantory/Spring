package umc.plantory.domain.chat.adapter.in;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.plantory.domain.chat.adapter.in.dto.ChatReqDto;
import umc.plantory.domain.chat.port.in.ChatCommandUseCase;
import umc.plantory.global.apiPayload.ApiResponse;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatCommandController implements ChatCommandApi {
    private final ChatCommandUseCase chatCommandUseCase;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<String>> chat(@RequestBody @Valid ChatReqDto.ChatRequest request, @RequestParam("memberId") Long memberId) {
        String response = chatCommandUseCase.ask(request.message(), memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
