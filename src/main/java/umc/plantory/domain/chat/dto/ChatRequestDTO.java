package umc.plantory.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ChatRequestDTO {
    @Schema(description = "채팅 내용", example = "오늘 너무 우울해", maxLength = 500)
    @NotBlank(message = "채팅 내용은 필수입니다")
    @Size(max = 500, message = "채팅 내용은 500자 이하여야 합니다")
    private String content;
}
