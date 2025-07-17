package umc.plantory.domain.image.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import umc.plantory.domain.image.converter.ImageConverter;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.ImageHandler;
import umc.plantory.domain.image.dto.request.PresignedUrlRequestDTO;
import umc.plantory.domain.image.dto.response.PresignedUrlResponseDTO;

import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    // 지원하는 파일 확장자와 MIME 타입 매핑
    private static final Map<String, String> EXTENSION_TO_MIME = Map.of(
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png"
    );

    // presigned Url 생성
    @Override
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

        return ImageConverter.toPresignedUrlResponseDTO(presignedUrl.toString(), accessUrl);
    }

    // S3에 해당 이미지가 존재하는지 확인
    @Override
    public void validateImageExistence(String imageUrl) {
        try {
            String key = extractKeyFromUrl(imageUrl);
            if (!amazonS3.doesObjectExist(bucket, key)) {
                throw new ImageHandler(ErrorStatus.IMAGE_NOT_FOUND);
            }
        } catch (Exception e) {
            throw new ImageHandler(ErrorStatus.IMAGE_NOT_FOUND);
        }
    }

    // 파일명에서 확장자를 추출하고 유효성을 검증
    private String extractAndValidateExtension(String fileName) {
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot == -1 || lastDot == fileName.length() - 1) {
            throw new ImageHandler(ErrorStatus.INVALID_FILENAME);
        }
        String extension = fileName.substring(lastDot + 1).toLowerCase();
        if (!EXTENSION_TO_MIME.containsKey(extension)) {
            throw new ImageHandler(ErrorStatus.INVALID_EXTENSION);
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

    // 이미지 URL로부터 key를 반환
    private String extractKeyFromUrl(String imageUrl) {
        String prefix = String.format("https://%s.s3.%s.amazonaws.com/", bucket, region);
        if (!imageUrl.startsWith(prefix)) {
            throw new ImageHandler(ErrorStatus.IMAGE_NOT_FOUND);
        }
        return imageUrl.substring(prefix.length());
    }
}