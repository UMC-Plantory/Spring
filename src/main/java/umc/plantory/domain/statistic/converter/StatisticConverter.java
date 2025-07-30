package umc.plantory.domain.statistic.converter;

import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.statistic.dto.response.StatisticResponseDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class StatisticConverter {

    public static StatisticResponseDTO.WeeklySleepStatisticDTO toWeeklyStatisticDTO(LocalDate today, int averageSleepMinutes, List<StatisticResponseDTO.DailySleepData> dailySleepData) {

        return StatisticResponseDTO.WeeklySleepStatisticDTO.builder()
                .hasDate(true)
                .startDate(today.minusDays(6))
                .endDate(today)
                .todayWeekday(String.valueOf(today.getDayOfWeek()))
                .averageSleepMinutes((long) averageSleepMinutes)
                .dailySleepRecords(dailySleepData)
                .build();
    }

    // 7일 일기 없음
    public static StatisticResponseDTO.WeeklySleepStatisticDTO toEmptyDTO(LocalDate today) {

        return StatisticResponseDTO.WeeklySleepStatisticDTO.builder()
                .hasDate(false)
                .startDate(today.minusDays(6))
                .endDate(today)
                .todayWeekday(String.valueOf(today.getDayOfWeek()))
                .averageSleepMinutes(0L)
                .dailySleepRecords(new ArrayList<>())
                .build();
    }

    // 일기를 작성한 날
    public static StatisticResponseDTO.DailySleepData toDailySleepData(Diary diary) {

        return StatisticResponseDTO.DailySleepData.builder()
                .date(diary.getDiaryDate())
                .weekday(String.valueOf(diary.getDiaryDate().getDayOfWeek()))
                .sleepStartTime(LocalTime.from(diary.getSleepStartTime()))
                .sleepEndTime(LocalTime.from(diary.getSleepEndTime()))
                .build();
    }

    // 일기를 작성하지 않은 날
    public static StatisticResponseDTO.DailySleepData toDailySleepData(LocalDate targetDate) {

        return StatisticResponseDTO.DailySleepData.builder()
                .date(targetDate)
                .weekday(String.valueOf(targetDate.getDayOfWeek()))
                .sleepStartTime(LocalTime.MIDNIGHT)
                .sleepEndTime(LocalTime.MIDNIGHT)
                .build();
    }
}
