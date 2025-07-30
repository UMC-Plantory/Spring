package umc.plantory.domain.statistic.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


public class StatisticResponseDTO {

    @Getter
    @Builder
    public static class WeeklySleepStatisticDTO {
        private boolean hasDate;
        private LocalDate startDate;
        private LocalDate endDate;
        private String todayWeekday;
        private Long averageSleepMinutes;
        private List<DailySleepData> dailySleepRecords;
    }

    @Getter
    @Builder
    public static class MonthlySleepStatisticDTO {
        private LocalDate startDate;
        private LocalDate endDate;
        private String todayWeekday;
        private Long averageSleepMinutes;
        private List<WeeklySleepData> weeklySleepRecords;
    }

    @Getter
    @Builder
    public static class DailySleepData {
        private LocalDate date;
        private String weekday;
        private LocalTime sleepStartTime;
        private LocalTime sleepEndTime;
    }

    @Getter
    @Builder
    public static class WeeklySleepData {
        private Long week;
        private LocalTime sleepStartTime;
        private LocalTime sleepEndTime;
    }
}
