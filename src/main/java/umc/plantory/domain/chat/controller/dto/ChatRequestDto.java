package umc.plantory.domain.chat.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

public class ChatRequestDto {

    /**
     * 채팅 메시지 요청 DTO
     * 챗봇에게 전송할 메시지 정보를 담는 클래스
     */
    @Schema(description = "채팅 요청 DTO")
    @Getter
    public static class ChatRequest {
        /**
         * 채팅 메시지 내용
         * 최대 400자까지 입력 가능
         */
        @Schema(
            description = "채팅 메시지 내용",
            example = "오늘 너무 우울해",
            maxLength = 400
        )
        @NotBlank(message = "메시지를 입력해주세요.")
        @Size(max = 400, message = "메시지는 최대 400자까지 입력할 수 있습니다.")
        private String content;

        /**
         * ChatRequest 생성자
         * 
         * @param content 채팅 메시지 내용
         */
        public ChatRequest(String content) {
            this.content = content;
        }
    }

}
