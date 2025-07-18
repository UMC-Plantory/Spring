package umc.plantory.domain.diary.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import umc.plantory.global.validation.annotation.ValidDiaryFields;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ValidDiaryFields
public class DiaryRequestDTO {

    @NotNull(message = "diaryDate 항목은 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate diaryDate;

    @Pattern(regexp = "HAPPY|SAD|SOSO|ANGRY|AMAZING", message = "emotion 값이 유효하지 않습니다.")
    private String emotion;

    private String content;

    private String diaryImgUrl;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime sleepStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime sleepEndTime;

    @NotNull(message = "status 항목은 필수입니다.")
    @Pattern(regexp = "NORMAL|TEMP", message = "status 값이 유효하지 않습니다.")
    private String status;
}
