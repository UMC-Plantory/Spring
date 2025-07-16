package umc.plantory.domain.image.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import umc.plantory.domain.image.converter.PresignedUrlConverter;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.S3Handler;
import umc.plantory.domain.image.dto.request.PresignedUrlRequestDTO;
import umc.plantory.domain.image.dto.response.PresignedUrlResponseDTO;

import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * S3 Presigned URL을 생성하는 서비스 클래스.
 * 클라이언트가 직접 S3에 이미지를 업로드할 수 있도록
 * 제한된 시간 동안 유효한 URL을 생성.
 */
@Service
@RequiredArgsConstructor
public class PresignedUrlService {

    // 지원하는 파일 확장자와 MIME 타입 매핑
    private static final Map<String, String> EXTENSION_TO_MIME = Map.of(
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png"
    );

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    // presigned Url 생성
    public PresignedUrlResponseDTO createPresignedUrl(PresignedUrlRequestDTO request) {
        String extension = extractAndValidateExtension(request.getFileName());
        String fileName = generateFileName(request.getType(), request.getFileName());
        String mimeType = EXTENSION_TO_MIME.get(extension);

        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(getExpiration())
                .withContentType(mimeType);

        URL presignedUrl = amazonS3.generatePresignedUrl(urlRequest);
        String accessUrl = buildAccessUrl(fileName);

        return PresignedUrlConverter.toPresignedUrlResponseDTO(presignedUrl.toString(), accessUrl);
    }

    // 파일명에서 확장자를 추출하고 유효성을 검증
    private String extractAndValidateExtension(String fileName) {
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot == -1 || lastDot == fileName.length() - 1) {
            throw new S3Handler(ErrorStatus.INVALID_FILENAME);
        }
        String extension = fileName.substring(lastDot + 1).toLowerCase();
        if (!EXTENSION_TO_MIME.containsKey(extension)) {
            throw new S3Handler(ErrorStatus.INVALID_EXTENSION);
        }
        return extension;
    }

    // UUID를 포함한 최종 업로드 파일명을 생성
    private String generateFileName(String type, String originalFileName) {
        return String.format("%s/%s-%s", type, UUID.randomUUID(), originalFileName);
    }

    // S3에 접근 가능한 public URL을 생성
    private String buildAccessUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, fileName);
    }

    // Presigned URL의 만료 시간을 반환(3분)
    private Date getExpiration() {
        return new Date(System.currentTimeMillis() + 1000 * 60 * 3);
    }
}