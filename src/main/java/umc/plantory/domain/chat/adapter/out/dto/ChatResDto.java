package umc.plantory.domain.chat.adapter.out.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ChatResDto {

    // 챗봇과 나눈 이전 채팅 조회 DTO
    public record ChatResponse(
            @NotBlank String content,
            @NotNull LocalDateTime createAt,
            boolean isMember
    ) {}
}
