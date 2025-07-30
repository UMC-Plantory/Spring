package umc.plantory.domain.statistic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.diary.repository.DiaryRepository;
import umc.plantory.domain.statistic.converter.StatisticConverter;
import umc.plantory.domain.statistic.dto.response.StatisticResponseDTO;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatisticQueryService implements StatisticQueryUseCase {

    private final DiaryRepository diaryRepository;

    @Override
    public StatisticResponseDTO.WeeklySleepStatisticDTO getWeeklySleepStatistics(LocalDate today) {

        // 최근 7일 일기 데이터 조회 (today 포함)
        List<Diary> diaries = diaryRepository.findByMemberIdAndDiaryDateBetweenOrderByDiaryDateDesc(1L, today.minusDays(6), today);

        // 7일 일기 데이터 없는 경우 빈 통계 DTO 반환
        if (diaries.isEmpty()) {
            return StatisticConverter.toEmptyDTO(today);
        }

        // 날짜별 수면 데이터 생성
        List<StatisticResponseDTO.DailySleepData> dailySleepDataList = getDailySleepData(today, diaries);

        // 평균 수면 시간(분) 계산
        int averageSleepMinutes = getAverageSleepMinutes(dailySleepDataList, diaries);

        return StatisticConverter.toWeeklyStatisticDTO(today, averageSleepMinutes, dailySleepDataList);
    }

    // 평균 수면 시간 계산
    private int getAverageSleepMinutes(List<StatisticResponseDTO.DailySleepData> dailySleepDataList, List<Diary> diaries) {
        int totalMinutes = 0;

        for (StatisticResponseDTO.DailySleepData dailySleepData : dailySleepDataList) {
            totalMinutes += (int) calculateSleepMinutes(dailySleepData.getSleepStartTime(), dailySleepData.getSleepEndTime());
        }

        int averageSleepMinutes = totalMinutes / diaries.size();
        return averageSleepMinutes;
    }

    // 7일 수면 데이터를 날짜 기준으로 설정 (없느 날짜는 빈 데이터)
    private static List<StatisticResponseDTO.DailySleepData> getDailySleepData(LocalDate today, List<Diary> diaries) {
        List<StatisticResponseDTO.DailySleepData> dailySleepDataList = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate targetDate = today.minusDays(6 - i);

            boolean found = false;

            for (Diary diary : diaries) {
                if (diary.getDiaryDate().equals(targetDate)) {
                    dailySleepDataList.add(StatisticConverter.toDailySleepData(diary));
                    found = true;
                    break;
                }
            }

            // 해당 날짜에 작성된 일기가 없는 경우
            if (!found) {
                dailySleepDataList.add(StatisticConverter.toDailySleepData(targetDate));
            }
        }
        return dailySleepDataList;
    }

    // 수면 시간(분) 계산 (자정 고려)
    private long calculateSleepMinutes(LocalTime start, LocalTime end) {

        long sleepMinutes = Duration.between(start, end).toMinutes();

        return (sleepMinutes < 0) ? sleepMinutes + 24 * 60 : sleepMinutes;
    }
}
