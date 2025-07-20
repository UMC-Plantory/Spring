package umc.plantory.domain.diary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.plantory.global.enums.DiaryStatus;
import umc.plantory.global.enums.Emotion;

import java.time.LocalDate;

public class DiaryResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiaryInfoDTO {
        private Long diaryId;
        private LocalDate diaryDate;
        private Emotion emotion;
        private String title;
        private String content;
        private String diaryImgUrl;
        private DiaryStatus status;
    }
}
