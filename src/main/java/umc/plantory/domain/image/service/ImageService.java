package umc.plantory.domain.image.service;

import umc.plantory.domain.image.dto.request.PresignedUrlRequestDTO;
import umc.plantory.domain.image.dto.response.PresignedUrlResponseDTO;

public interface ImageService {
    PresignedUrlResponseDTO createPresignedUrl(PresignedUrlRequestDTO request);
    void validateImageExistence(String imageUrl);
    void deleteImage(String imageUrl);
}