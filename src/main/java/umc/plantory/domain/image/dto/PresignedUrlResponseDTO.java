package umc.plantory.domain.image.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PresignedUrlResponseDTO {
    private String presignedUrl;
    private String accessUrl;
}
