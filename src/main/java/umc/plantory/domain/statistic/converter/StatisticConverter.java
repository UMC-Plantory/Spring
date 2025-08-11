package umc.plantory.domain.statistic.converter;

import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.statistic.dto.StatisticResponseDTO;
import umc.plantory.global.enums.Emotion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class StatisticConverter {

    // 7일 수면 통계
    public static StatisticResponseDTO.WeeklySleepStatisticDTO toWeeklyStatisticDTO(
            LocalDate startDate,
            LocalDate endDate,
            Integer averageSleepMinutes,
            List<StatisticResponseDTO.DailySleepRecord> dailySleepData) {

        return StatisticResponseDTO.WeeklySleepStatisticDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .todayWeekday(endDate.getDayOfWeek())
                .averageSleepMinutes(averageSleepMinutes)
                .dailySleepRecords(dailySleepData)
                .build();
    }

    // 1일 수면 정보
    public static StatisticResponseDTO.DailySleepRecord toDailySleepData(Integer indexDay, Diary diary) {

        return StatisticResponseDTO.DailySleepRecord.builder()
                .day(indexDay)
                .date(diary.getDiaryDate())
                .weekday(diary.getDiaryDate().getDayOfWeek())
                .sleepStartTime(LocalTime.from(diary.getSleepStartTime()))
                .sleepEndTime(LocalTime.from(diary.getSleepEndTime()))
                .build();
    }

    // 1일 수면 정보 (수면 정보 없음)
    public static StatisticResponseDTO.DailySleepRecord toEmptyDailySleepData(Integer indexDay, LocalDate targetDate) {

        return StatisticResponseDTO.DailySleepRecord.builder()
                .day(indexDay)
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
            List<StatisticResponseDTO.WeeklySleepRecord> weeklySleepRecordList) {

        return StatisticResponseDTO.MonthlySleepStatisticDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .todayWeekday(endDate.getDayOfWeek())
                .averageSleepMinutes(averageSleepMinutes)
                .weeklySleepRecords(weeklySleepRecordList)
                .build();
    }

    // 1주일 수면 정보
    public static StatisticResponseDTO.WeeklySleepRecord toWeeklySleepData(
            Integer week,
            LocalTime startTime,
            LocalTime endTime) {

        return  StatisticResponseDTO.WeeklySleepRecord.builder()
                .week(week)
                .averageSleepStartTime(startTime)
                .averageSleepEndTime(endTime)
                .build();

    }

    // 감정 통계
    public static StatisticResponseDTO.EmotionStatisticDTO toEmotionStatisticDTO(
            LocalDate startDate,
            LocalDate endDate,
            Emotion mostFrequentEmotion,
            Map<Emotion, Integer> emotionMap
    ) {

        return StatisticResponseDTO.EmotionStatisticDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .todayWeekday(endDate.getDayOfWeek())
                .mostFrequentEmotion(mostFrequentEmotion)
                .emotionFrequency(emotionMap)
                .build();
    }
}
