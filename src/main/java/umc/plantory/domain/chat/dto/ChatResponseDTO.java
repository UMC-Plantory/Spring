package umc.plantory.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.MessageType;

import java.time.LocalDateTime;

public class ChatResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatResponse {
        @Schema(description = "메시지 내용")
        String content;
        @Schema(description = "생성 시간", example = "2025-07-20T12:00:00")
        LocalDateTime createAt;
        @Schema(description = "사용자 요청인지 챗봇 응답인지", example = "USER")
        MessageType messageType;
    }
}
