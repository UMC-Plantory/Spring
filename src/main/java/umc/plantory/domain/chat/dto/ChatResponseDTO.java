package umc.plantory.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDTO {
    @Schema(description = "메시지 내용")
    private String content;
    @Schema(description = "생성 시간", example = "2025-07-20T12:00:00")
    private LocalDateTime createdAt;
    @Schema(description = "사용자 요청인지 챗봇 응답인지", example = "false")
    private Boolean isMember;
}
