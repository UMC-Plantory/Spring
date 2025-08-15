package umc.plantory.domain.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

public class DiaryProjectionDTO {

    @Getter
    @AllArgsConstructor
    public static class SleepIntervalDTO {
        private Long memberId;
        private LocalDateTime sleepStartTime;
        private LocalDateTime sleepEndTime;
    }
}
