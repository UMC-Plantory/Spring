package umc.plantory.domain.image.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class PresignedUrlRequestDTO {

    @NotBlank(message = "type은 필수입니다.")
    @Pattern(regexp = "^(profile|diary)$", message = "type은 profile 또는 diary만 가능합니다.")
    private String type;

    @NotBlank(message = "fileName은 필수입니다.")
    private String fileName;
}
