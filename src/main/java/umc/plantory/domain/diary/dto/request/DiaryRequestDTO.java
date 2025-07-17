package umc.plantory.domain.diary.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import umc.plantory.global.enums.DiaryStatus;
import umc.plantory.global.enums.Emotion;
import umc.plantory.global.validation.annotation.ValidDiaryFields;
import umc.plantory.global.validation.annotation.ValidEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ValidDiaryFields
public class DiaryRequestDTO {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate diaryDate;

    @ValidEnum(enumClass = Emotion.class, message = "emotion 값이 유효하지 않습니다.")
    private String emotion;

    private String content;

    private String diaryImgUrl;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime sleepStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime sleepEndTime;

    @NotNull(message = "status는 필수입니다.")
    @ValidEnum(enumClass = DiaryStatus.class, message = "status 값이 유효하지 않습니다.")
    private String status;
}
