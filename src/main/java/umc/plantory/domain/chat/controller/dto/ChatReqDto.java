package umc.plantory.domain.chat.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChatReqDto {

    /**
     * 채팅 메시지 요청 DTO
     * @param content 채팅 메시지 내용
     */
    public record ChatRequest (
        @NotBlank(message = "메시지를 입력해주세요.")
        @Size(max = 400, message = "메시지는 최대 400자까지 입력할 수 있습니다.")
        String content
    ) {}

}
