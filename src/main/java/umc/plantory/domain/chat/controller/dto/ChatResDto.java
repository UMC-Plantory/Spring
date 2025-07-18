package umc.plantory.domain.chat.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ChatResDto {

    /**
     * 챗봇과 나눈 이전 채팅 조회 응답 DTO
     * @param content 채팅 메시지 내용
     * @param createAt 생성 시각
     * @param isMember true: 사용자, false: 챗봇
     */
    public record ChatResponse(
            @NotBlank String content,
            @NotNull LocalDateTime createAt,
            boolean isMember
    ) {}
}
