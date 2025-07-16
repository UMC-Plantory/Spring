package umc.plantory.domain.image.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.plantory.global.apiPayload.ApiResponse;
import umc.plantory.domain.image.dto.request.PresignedUrlRequestDTO;
import umc.plantory.domain.image.dto.response.PresignedUrlResponseDTO;
import umc.plantory.domain.image.service.PresignedUrlService;

@Tag(name = "Image", description = "AWS S3 Presigned URL 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/plantory")
public class PresignedUrlController {

    private final PresignedUrlService presignedUrlService;

    @Operation(
            summary = "Presigned URL 발급",
            description = """
                    이미지 파일을 S3에 직접 업로드할 수 있도록 제한된 시간의 Presigned URL을 발급합니다.
                    발급된 URL에 PUT 요청으로 이미지를 업로드하면, accessUrl을 통해 이미지에 접근할 수 있습니다.
                    """
    )
    @PostMapping("/presigned-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponseDTO>> getPresignedUrl(
            @Valid @RequestBody PresignedUrlRequestDTO request
    ) {
        PresignedUrlResponseDTO response = presignedUrlService.createPresignedUrl(request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}