package umc.plantory.domain.chat.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

public class ChatResponseDto {

    /**
     * 챗봇과 나눈 이전 채팅 조회 응답 DTO
     */
    @Schema(description = "채팅 응답 DTO")
    @Getter
    public static class ChatResponse {
        /**
         * 채팅 메시지 내용
         */
        @Schema(description = "메시지 내용")
        private String content;
        
        /**
         * 채팅 메시지 생성 시간
         */
        @Schema(description = "생성 시간", example = "2025-07-20T12:00:00")
        private LocalDateTime createAt;
        
        /**
         * 메시지 발신자 구분
         * true: 사용자, false: 챗봇
         */
        @Schema(description = "사용자 요청인지 챗봇 응답인지", example = "true")
        private boolean isMember;

        /**
         * ChatResponse 생성자
         * 
         * @param content 채팅 메시지 내용
         * @param createAt 생성 시간
         * @param isMember 메시지 발신자 구분 (true: 사용자, false: 챗봇)
         */
        public ChatResponse(String content, LocalDateTime createAt, boolean isMember) {
            this.content = content;
            this.createAt = createAt;
            this.isMember = isMember;
        }
    }
}
