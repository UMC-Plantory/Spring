package umc.plantory.domain.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.plantory.global.enums.DiaryStatus;
import umc.plantory.global.enums.Emotion;

import java.time.LocalDate;
import java.util.List;

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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CursorPaginationDTO<T> {
        private List<T> diaries;
        private boolean hasNext;
        private LocalDate nextCursor;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CursorPaginationTotalDTO<T> {
        private List<T> diaries;
        private boolean hasNext;
        private LocalDate nextCursor;
        private long total;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DiaryListDTO {
        private List<DiaryListSimpleInfoDTO> diaries;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiaryListInfoDTO {
        private Long diaryId;
        private LocalDate diaryDate;
        private String title;
        private DiaryStatus status;
        private Emotion emotion;
        private String content;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiaryListSimpleInfoDTO {
        private Long diaryId;
        private LocalDate diaryDate;
        private String title;
    }
}
