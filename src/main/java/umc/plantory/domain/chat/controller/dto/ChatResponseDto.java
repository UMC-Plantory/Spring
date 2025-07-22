package umc.plantory.domain.chat.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ChatResponseDto {

    /**
     * 챗봇과 나눈 이전 채팅 조회 응답 DTO
     * @param content 채팅 메시지 내용
     * @param createAt 생성 시각
     * @param isMember true: 사용자, false: 챗봇
     */
    public record ChatResponse(
            @Schema(description = "메시지 내용")
            String content,
            @Schema(description = "생성 시간", example = "2025-07-20T12:00:00")
            LocalDateTime createAt,
            @Schema(description = "사용자 요청인지 챗봇 응답인지", example = "true")
            boolean isMember
    ) {}
}
