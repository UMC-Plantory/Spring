package umc.plantory.domain.statistic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.plantory.global.enums.Emotion;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;


public class StatisticResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklySleepStatisticDTO {
        private LocalDate startDate;
        private LocalDate endDate;
        private DayOfWeek todayWeekday;
        private Integer averageSleepMinutes;
        private List<DailySleepRecord> dailySleepRecords;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailySleepRecord {
        private Integer day;
        private LocalDate date;
        private DayOfWeek weekday;
        private LocalTime sleepStartTime;
        private LocalTime sleepEndTime;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlySleepStatisticDTO {
        private LocalDate startDate;
        private LocalDate endDate;
        private DayOfWeek todayWeekday;
        private Integer averageSleepMinutes;
        private List<WeeklySleepRecord> weeklySleepRecords;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklySleepRecord {
        private Integer week;
        private LocalTime averageSleepStartTime;
        private LocalTime averageSleepEndTime;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmotionStatisticDTO {
        private LocalDate startDate;
        private LocalDate endDate;
        private DayOfWeek todayWeekday;
        private Emotion mostFrequentEmotion;
        private Map<Emotion, Integer> emotionFrequency;
    }
}
