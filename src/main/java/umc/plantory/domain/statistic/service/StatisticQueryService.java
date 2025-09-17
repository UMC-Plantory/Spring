package umc.plantory.domain.statistic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.diary.repository.DiaryRepository;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.statistic.converter.StatisticConverter;
import umc.plantory.domain.statistic.dto.StatisticResponseDTO;
import lombok.extern.slf4j.Slf4j;
import umc.plantory.domain.token.provider.JwtProvider;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.MemberHandler;
import umc.plantory.global.apiPayload.exception.handler.StatisticHandler;
import umc.plantory.global.enums.Emotion;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static umc.plantory.global.enums.DiaryStatus.VALID_STATUSES;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatisticQueryService implements StatisticQueryUseCase {

    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    /**
     * 전달받은 날짜를 기준으로, 사용자의 최근 7일간 수면 통계 정보를 반환합니다.
     * @param authorization 헤더에서 받아온 사용자 인증 정보
     * @param today 기준 날짜
     * @return WeeklySleepStatisticDTO - 날짜 별 평균 수면 시간 및 7일간 평균 수면 시간
     */
    @Override
    public StatisticResponseDTO.WeeklySleepStatisticDTO getWeeklySleepStatistics(String authorization, LocalDate today) {

        // member 받아오기
        Member member = getLoginedMember(authorization);

        // 시작일 및 종료일 설정
        LocalDate startDate = today.minusDays(6);
        LocalDate endDate = today;

        // 최근 7일 일기 데이터 조회 (startDate 부터 endDate 까지)
        List<Diary> diaries = diaryRepository.findByMemberAndStatusInAndDiaryDateBetween(member, VALID_STATUSES, startDate, endDate);

        // 데이터 없는 경우 예외처리
        if (diaries.isEmpty()) {
            throw new StatisticHandler(ErrorStatus.SLEEP_STATISTIC_NOT_FOUND);
        }

        // 일기 데이터를 날짜 기준으로 매핑 (시간 복잡도 고려)
        Map<LocalDate, Diary> diaryMap = new HashMap<>();
        for (Diary diary : diaries) {
            diaryMap.put(diary.getDiaryDate(), diary);
        }

        // 날짜별 수면 데이터 리스트
        List<StatisticResponseDTO.DailySleepData> dailySleepDataList = new ArrayList<>();

        // 날짜별 수면 데이터 생성 (작성된 일기가 없는 경우, 빈 데이터로 채움)
        for (int i = 0; i < 7; i++) {
            LocalDate targetDate = endDate.minusDays(6 - i);
            Diary diary = diaryMap.get(targetDate);

            if (diary != null) {
                dailySleepDataList.add(StatisticConverter.toDailySleepData(i, diary));
            } else {
                dailySleepDataList.add(StatisticConverter.toEmptyDailySleepData(i, targetDate));
            }
        }

        // 7일 평균 수면 시간 계산 (단위: 분)
        Integer averageSleepMinutes = calculateAverageSleepMinutes(diaries);

        return StatisticConverter.toWeeklyStatisticDTO(startDate, endDate, averageSleepMinutes, dailySleepDataList);
    }

    /**
     * 전달받은 날짜를 기준으로, 사용자의 최근 30일간 수면 통계 정보를 반환합니다.
     * @param authorization 헤더에서 받아온 사용자 인증 정보
     * @param today 기준 날짜
     * @return MonthlySleepStatisticDTO - 주별 평균 수면 시각 및 30일간 평균 수면 시간
     */

    @Override
    public StatisticResponseDTO.MonthlySleepStatisticDTO getMonthlySleepStatistics(String authorization, LocalDate today) {

        Member member = getLoginedMember(authorization);

        // 시작일 및 종료일 설정
        LocalDate startDate = today.minusDays(29);
        LocalDate endDate = today;

        // 최근 30일 일기 데이터 조회 (startDate 부터 endDate 까지)
        List<Diary> diaries = diaryRepository.findByMemberAndStatusInAndDiaryDateBetween(member, VALID_STATUSES, startDate, endDate);

        // 데이터 없는 경우 예외처리
        if (diaries.isEmpty()) {
            throw new StatisticHandler(ErrorStatus.SLEEP_STATISTIC_NOT_FOUND);
        }

        // 일기 데이터를 날짜 기준으로 매핑 (시간 복잡도 고려)
        Map<LocalDate, Diary> diaryMap = new HashMap<>();
        for (Diary diary : diaries) {
            diaryMap.put(diary.getDiaryDate(), diary);
        }

        // 각 주차별 수면 데이터 리스트
        List<StatisticResponseDTO.WeeklySleepData> weeklySleepDataList = new ArrayList<>();

        // 각 주차별 평균 수면 시각 및 기상 시각 구하기
        for (int i = 0; i < 5; i++) {
            List<LocalTime> sleepStartTimes = new ArrayList<>();
            List<LocalTime> sleepEndTimes = new ArrayList<>();

            for (int j = i * 7; j < (i + 1) * 7; j++) {
                LocalDate targetDate = startDate.plusDays(j);
                if (targetDate.isAfter(endDate)) break;

                Diary diary = diaryMap.get(targetDate);
                if (diary == null) continue;

                sleepStartTimes.add(LocalTime.from(diary.getSleepStartTime()));
                sleepEndTimes.add(LocalTime.from(diary.getSleepEndTime()));
            }

            // 평균 수면 시각 및 평균 기상 시각 계산
            LocalTime averageSleepStartTime = calculateAverageTime(sleepStartTimes);
            LocalTime averageSleepEndTime = calculateAverageTime(sleepEndTimes);

            // 주차별 수면 데이터 생성
            StatisticResponseDTO.WeeklySleepData weeklySleepData = StatisticConverter.toWeeklySleepData(
                    i + 1,averageSleepStartTime, averageSleepEndTime);

            // 주차별 수면 데이터 리스트에 추가
            weeklySleepDataList.add(weeklySleepData);
        }

        // 30일 평균 수면 시간 계산 (단위: 분)
        Integer averageSleepMinutes = calculateAverageSleepMinutes(diaries);

        return StatisticConverter.toMonthlySleepStatisticDTO(startDate, endDate, averageSleepMinutes, weeklySleepDataList);
    }

    /**
     * 전달받은 날짜를 기준으로, 최근 range일간의 감정 통계 정보를 반환합니다.
     * @param authorization 헤더에서 받아온 사용자 인증 정보
     * @param today 기준 날짜
     * @param range 범위 (ex: range = 7 이면, 최근 7일간의 범위)
     * @return EmotionStatisticDTO - 감정별 빈도수 및 최다 감정 포함
     */

    @Override
    public StatisticResponseDTO.EmotionStatisticDTO getEmotionStatistics(String authorization, LocalDate today, Integer range) {

        Member member = getLoginedMember(authorization);

        // 시작일 및 종료일 설정
        LocalDate startDate = today.minusDays(range - 1);
        LocalDate endDate = today;

        // 최근 일기 데이터 조회 (startDate 부터 endDate 까지)
        List<Diary> diaries = diaryRepository.findByMemberAndStatusInAndDiaryDateBetween(member, VALID_STATUSES,startDate, endDate);

        // 데이터 없는 경우 예외처리
        if (diaries.isEmpty()) {
            throw new StatisticHandler(ErrorStatus.EMOTION_STATISTIC_NOT_FOUND);
        }

        // 감정 값 0으로 초기화
        Map<Emotion, Integer> emotionMap = new EnumMap<>(Emotion.class);
        for (Emotion emotion : Emotion.values()) {
            emotionMap.put(emotion, 0);
        }

        // 감정 빈도 계산
        for (Diary diary : diaries) {
            emotionMap.put(diary.getEmotion(), emotionMap.get(diary.getEmotion()) + 1);
        }

        // 최다 감정 계산 (동률일 경우 랜덤으로 선택)
        Emotion mostFrequentEmotion = selectMostFrequentEmotion(emotionMap);

        return StatisticConverter.toEmotionStatisticDTO(startDate, endDate, mostFrequentEmotion, emotionMap);
    }

    // 평균 수면 시간(분) 계산
    private Integer calculateAverageSleepMinutes(List<Diary> diaries) {
        int totalMinutes = 0;

        for (Diary diary : diaries) {
            totalMinutes += (int) Duration.between(diary.getSleepStartTime(), diary.getSleepEndTime()).toMinutes();
        }

        return totalMinutes / diaries.size();
    }

    // 평균 수면 시각 계산 (23시 ~ 1시 같이 경계에 걸친 시간 고려)
    private LocalTime calculateAverageTime(List<LocalTime> times) {
        if (times.isEmpty()){
            return LocalTime.MIDNIGHT;
        }

        double sumSin = 0.0;
        double sumCos = 0.0;

        for (LocalTime time : times) {
            double angle = (time.toSecondOfDay() / 86400.0) * 2 * Math.PI;
            sumSin += Math.sin(angle);
            sumCos += Math.cos(angle);
        }

        double avgAngle = Math.atan2(sumSin / times.size(), sumCos / times.size());
        if (avgAngle < 0) avgAngle += 2 * Math.PI;

        int avgSeconds = (int) ((avgAngle / (2 * Math.PI)) * 86400);
        return LocalTime.ofSecondOfDay(avgSeconds);
    }

    // 최다 감정 계산 (동률일 경우 랜덤으로 선택)
    private Emotion selectMostFrequentEmotion(Map<Emotion, Integer> emotionMap) {
        List<Emotion> mostFrequentEmotions = new ArrayList<>();
        int max = -1;

        for (Map.Entry<Emotion, Integer> entry : emotionMap.entrySet()) {
            int count = entry.getValue();

            if (count > max) {
                mostFrequentEmotions.clear();
                mostFrequentEmotions.add(entry.getKey());
                max = count;
            } else if (entry.getValue() == max) {
                mostFrequentEmotions.add(entry.getKey());
            }
        }
        return mostFrequentEmotions.get(new Random().nextInt(mostFrequentEmotions.size()));
    }

    // 로그인한 사용자 정보 받아오기
    private Member getLoginedMember(String authorization) {
        String token = jwtProvider.resolveToken(authorization);
        if (token == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        jwtProvider.validateToken(token);
        Long memberId = jwtProvider.getMemberId(token);

        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }
}
