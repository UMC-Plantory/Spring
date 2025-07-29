package umc.plantory.domain.image.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class PresignedUrlRequestDTO {

    @Schema(description = "사진의 종류(profile, diary)", example = "profile")
    @NotBlank(message = "type은 필수입니다.")
    @Pattern(regexp = "^(profile|diary)$", message = "type은 profile 또는 diary만 가능합니다.")
    private String type;

    @Schema(description = "확장자를 포함한 파일명", example = "test.jpg")
    @NotBlank(message = "fileName은 필수입니다.")
    private String fileName;
}
