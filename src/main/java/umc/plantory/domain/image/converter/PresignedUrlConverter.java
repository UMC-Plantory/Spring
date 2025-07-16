package umc.plantory.domain.image.converter;

import umc.plantory.domain.image.dto.response.PresignedUrlResponseDTO;

public class PresignedUrlConverter {

    public static PresignedUrlResponseDTO toPresignedUrlResponseDTO(String presignedUrl, String accessUrl) {
        return PresignedUrlResponseDTO.builder()
                .presignedUrl(presignedUrl)
                .accessUrl(accessUrl)
                .build();
    }
}