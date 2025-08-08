package umc.plantory.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
public class ChatRequestDTO {
    @Schema(description = "채팅 내용", example = "오늘 너무 우울해", maxLength = 400)
    @NotNull(message = "채팅 내용은 필수입니다")
    private String content;
}
