package umc.plantory.domain.image.service;

import umc.plantory.domain.image.dto.PresignedUrlRequestDTO;
import umc.plantory.domain.image.dto.PresignedUrlResponseDTO;

public interface ImageUseCase {
    PresignedUrlResponseDTO createPresignedUrl(PresignedUrlRequestDTO request);
    void validateImageExistence(String imageUrl);
    void deleteImage(String imageUrl);
}