package umc.plantory.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @Schema(description = "챗봇 답변")
    private String content;

    @Schema(description = "채팅 시간", example = "2025-07-20T12:34")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime createdAt;

    @Schema(description = "사용자 요청인지 챗봇 응답인지", example = "false")
    private Boolean isMember;
}
