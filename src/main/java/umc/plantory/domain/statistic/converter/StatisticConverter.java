package umc.plantory.domain.statistic.converter;

import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.statistic.dto.response.StatisticResponseDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class StatisticConverter {

    // 7일 수면 통계
    public static StatisticResponseDTO.WeeklySleepStatisticDTO toWeeklyStatisticDTO(
            LocalDate startDate,
            LocalDate endDate,
            Integer averageSleepMinutes,
            List<StatisticResponseDTO.DailySleepData> dailySleepData) {

        return StatisticResponseDTO.WeeklySleepStatisticDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .todayWeekday(endDate.getDayOfWeek())
                .averageSleepMinutes(averageSleepMinutes)
                .dailySleepRecords(dailySleepData)
                .build();
    }

    // 1일 수면 정보
    public static StatisticResponseDTO.DailySleepData toDailySleepData(Diary diary) {

        return StatisticResponseDTO.DailySleepData.builder()
                .date(diary.getDiaryDate())
                .weekday(diary.getDiaryDate().getDayOfWeek())
                .sleepStartTime(LocalTime.from(diary.getSleepStartTime()))
                .sleepEndTime(LocalTime.from(diary.getSleepEndTime()))
                .build();
    }

    // 1일 수면 정보 (수면 정보 없음)
    public static StatisticResponseDTO.DailySleepData toEmptyDailySleepData(LocalDate targetDate) {

        return StatisticResponseDTO.DailySleepData.builder()
                .date(targetDate)
                .weekday(targetDate.getDayOfWeek())
                .sleepStartTime(LocalTime.MIDNIGHT)
                .sleepEndTime(LocalTime.MIDNIGHT)
                .build();
    }

    // 30일 수면 통계
    public static StatisticResponseDTO.MonthlySleepStatisticDTO toMonthlySleepStatisticDTO(
            LocalDate startDate,
            LocalDate endDate,
            Integer averageSleepMinutes,
            List<StatisticResponseDTO.WeeklySleepData> weeklySleepDataList) {

        return StatisticResponseDTO.MonthlySleepStatisticDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .todayWeekday(endDate.getDayOfWeek())
                .averageSleepMinutes(averageSleepMinutes)
                .weeklySleepRecords(weeklySleepDataList)
                .build();
    }

    // 1주일 수면 정보
    public static StatisticResponseDTO.WeeklySleepData toWeeklySleepData(
            Integer week,
            LocalTime startTime,
            LocalTime endTime) {

        return  StatisticResponseDTO.WeeklySleepData.builder()
                .week(week)
                .averageSleepStartTime(startTime)
                .averageSleepEndTime(endTime)
                .build();

    }
}
