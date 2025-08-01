package umc.plantory.domain.diary.dto;

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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiarySimpleInfoDTO {
        private Long diaryId;
        private LocalDate diaryDate;
        private Emotion emotion;
        private String title;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TempDiaryExistsDTO {
        private boolean isExist;
    }
}
