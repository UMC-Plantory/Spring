package umc.plantory.domain.diary.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import umc.plantory.global.validation.annotation.ValidDiaryFields;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class DiaryRequestDTO {

    @Getter
    @ValidDiaryFields
    public static class DiaryUploadDTO{

        @Schema(description = "일기 날짜", example = "2025-07-18", type = "string")
        @NotNull(message = "diaryDate 항목은 필수입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate diaryDate;

        @Schema(description = "감정 (HAPPY, SAD, SOSO, ANGRY, AMAZING)", example = "HAPPY")
        @Pattern(regexp = "HAPPY|SAD|SOSO|ANGRY|AMAZING", message = "emotion 값이 유효하지 않습니다.")
        private String emotion;

        @Schema(description = "일기 본문 내용", example = "오늘 하루는 ...")
        private String content;

        @Schema(description = "이미지 URL", example = "https://...example.jpg")
        private String diaryImgUrl;

        @Schema(description = "잠든 시간", example = "2025-07-17T23:00", type = "string")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        private LocalDateTime sleepStartTime;

        @Schema(description = "기상 시간", example = "2025-07-18T07:30", type = "string")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        private LocalDateTime sleepEndTime;

        @Schema(description = "저장 상태 (NORMAL: 정식 저장, TEMP: 임시 저장)", example = "NORMAL")
        @NotNull(message = "status 항목은 필수입니다.")
        @Pattern(regexp = "NORMAL|TEMP", message = "status 값이 유효하지 않습니다.")
        private String status;
    }

    @Getter
    public static class DiaryUpdateDTO{

        @Schema(description = "감정 (HAPPY, SAD, SOSO, ANGRY, AMAZING)", example = "HAPPY")
        @Pattern(regexp = "HAPPY|SAD|SOSO|ANGRY|AMAZING", message = "emotion 값이 유효하지 않습니다.")
        private String emotion;

        @Schema(description = "일기 본문 내용", example = "오늘 하루는 ...")
        private String content;

        @Schema(description = "이미지 URL", example = "https://...example.jpg")
        private String diaryImgUrl;

        @Schema(description = "잠든 시간", example = "2025-07-17T23:00", type = "string")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        private LocalDateTime sleepStartTime;

        @Schema(description = "기상 시간", example = "2025-07-18T07:30", type = "string")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        private LocalDateTime sleepEndTime;

        @Schema(description = "저장 상태 (NORMAL: 정식 저장, TEMP: 임시 저장)", example = "NORMAL")
        @Pattern(regexp = "NORMAL|TEMP", message = "status 값이 유효하지 않습니다.")
        private String status;

        @Schema(description = "기존 이미지 삭제 여부", example = "false")
        private Boolean isImgDeleted;
    }

    @Getter
    public static class DiaryIdsDTO {
        @Schema(description = "요청할 일기 ID 배열", example = "[1, 2, 3]")
        @NotNull(message = "diaryIds 항목은 필수입니다.")
        private List<Long> diaryIds;
    }
}
