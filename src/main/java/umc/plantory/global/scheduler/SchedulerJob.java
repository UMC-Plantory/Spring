package umc.plantory.global.scheduler;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.diary.dto.DiaryProjectionDTO;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.diary.entity.DiaryImg;
import umc.plantory.domain.diary.repository.DiaryImgRepository;
import umc.plantory.domain.diary.repository.DiaryRepository;
import umc.plantory.domain.image.service.ImageUseCase;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.push.dto.PushDataDTO;
import umc.plantory.domain.push.repository.PushRepository;
import umc.plantory.domain.wateringCan.entity.WateringCan;
import umc.plantory.domain.wateringCan.repository.WateringCanRepository;
import umc.plantory.global.enums.DiaryStatus;
import umc.plantory.global.enums.MemberStatus;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static umc.plantory.global.enums.DiaryStatus.VALID_STATUSES;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SchedulerJob {

    private final DiaryRepository diaryRepository;
    private final DiaryImgRepository diaryImgRepository;
    private final WateringCanRepository wateringCanRepository;
    private final MemberRepository memberRepository;
    private final PushRepository pushRepository;
    private final ImageUseCase imageUseCase;

    private final static Integer DEFAULT_BADGE = 1;
    private final static String DEFAULT_SOUND = "default";
    private final static String DEFAULT_TITLE = "플랜토리";
    private final static String DEFAULT_CONTENT = "오늘 하루 어땠나요? 오늘 하루를 기록해볼까요 \uFE0F";
    private final static String THREE_DAYS_CONTENT = "잠깐 쉬어가도 괜찮아요\uD83D\uDE0A 오늘도 수고했어요!";
    private final static String FIVE_DAYS_CONTENT = "요즘 몸과 마음이 조금 지치진 않으셨나요? 작은 기록이 위로가 될 수 있어요\uD83C\uDF75";
    private final static String SEVEN_DAYS_CONTENT = "마지막 기록 이후 일주일이 지났어요! 다시 함께 식물을 키워볼까요\uD83C\uDF3C";
    private final static String FOURTEEN_DAYS_CONTENT = "마지막 기록 이후 2주가 지났어요! 다시 함께 식물을 키워볼까요\uD83C\uDF3C";
    private final static String THIRTY_DAYS_CONTENT = "마지막 기록 이후 한 달이 지났어요! 다시 함께 식물을 키워볼까요\uD83C\uDF3C";

    /**
     * 매일 자정 00:00에 TEMP 상태 중 30일 지난 일기를 DELETE 상태로 변경
     */
    @Transactional
    public void updateTempToDeleted() {
        log.info("[스케줄러] TEMP → DELETE 작업 시작");

        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDate threshold = LocalDate.now().minusDays(30);
            long updatedCount = diaryRepository.bulkUpdateTempToDeleted(threshold, now);

            log.info("[스케줄러] TEMP → DELETE 처리 완료 | 대상: {}건", updatedCount);
        } catch (Exception e) {
            log.error("[스케줄러] TEMP → DELETE 처리 실패", e);
        }
    }

    /**
     * 매일 자정 00:00에 DELETE 상태 중 30일 지난 일기를 영구 삭제 처리
     */
    @Transactional
    public void deleteDiariesPermanently() {
        log.info("[스케줄러] DELETE → 영구 삭제 작업 시작");

        try {
            LocalDate threshold = LocalDate.now().minusDays(30);

            // 30일전 휴지통으로 이동한 DELETE 상태의 일기 조회
            List<Diary> deletedDiaries = diaryRepository
                    .findByStatusAndDeletedAtBefore(DiaryStatus.DELETE, threshold.atStartOfDay());

            // 관련 DiaryImg, WateringCan 한 번에 조회
            List<DiaryImg> diaryImgs = diaryImgRepository.findByDiaryIn(deletedDiaries);
            List<WateringCan> wateringCans = wateringCanRepository.findByDiaryIn(deletedDiaries);

            // 이미지 삭제
            for (DiaryImg img : diaryImgs) {
                imageUseCase.deleteImage(img.getDiaryImgUrl());
            }
            diaryImgRepository.deleteAll(diaryImgs);

            // 물뿌리개 연결 해제
            for (WateringCan can : wateringCans) {
                can.updateDiary(null);
            }

            // 일기 삭제
            diaryRepository.deleteAll(deletedDiaries);

            log.info("[스케줄러] DELETE → 영구 삭제 처리 완료 | 대상: {}건", deletedDiaries.size());
        } catch (Exception e) {
            log.error("[스케줄러] DELETE → 영구 삭제 처리 실패", e);
        }
    }

    /**
     * 매일 자정 00:00에 어제 일기를 작성하지 않은 멤버를 찾아 continuousRecordCnt 0으로 초기화
     */
    @Transactional
    public void resetContinuousRecordCnt() {
        log.info("[스케줄러] 연속 기록 초기화 시작");

        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            long updatedCount = memberRepository.bulkUpdateContinuousRecordCnt(yesterday);

            log.info("[스케줄러] 연속 기록 초기화 완료 | 대상: {}명", updatedCount);
        } catch (Exception e) {
            log.error("[스케줄러] 연속 기록 초기화 실패", e);
        }
    }

    /**
     * 매일 자정 00:00에 최근 7일 평균 수면 시간 갱신
     */
    @Transactional
    public void updateAvgSleepTime() {
        log.info("[스케줄러] 최근 7일 평균 수면 시간 갱신 시작");

        try {
            LocalDate today = LocalDate.now();
            LocalDate start = today.minusDays(6);

            // ACTIVE 상태의 유저 조회
            List<Member> members = memberRepository.findAllByStatus(MemberStatus.ACTIVE);

            // 유저들의 최근 7일간 일기에서 수면 시작 시각과 수면 종료 시각을 한 번에 조회
            List<DiaryProjectionDTO.SleepIntervalDTO> intervals =
                    diaryRepository.findByMemberInAndStatusInAndDiaryDateBetween(members, VALID_STATUSES, start, today);

            // 유저별로 수면 데이터 그룹핑
            Map<Long, List<DiaryProjectionDTO.SleepIntervalDTO>> sleepIntervalByMemberId = intervals.stream()
                    .collect(Collectors.groupingBy(DiaryProjectionDTO.SleepIntervalDTO::getMemberId));

            int updatedCount = 0;

            // 각 유저에 대해 평균 수면 시간을 계산하고 필요 시 업데이트
            for (Member member : members) {
                List<DiaryProjectionDTO.SleepIntervalDTO> rows = sleepIntervalByMemberId.getOrDefault(member.getId(), Collections.emptyList());

                // 평균 수면 시간 계산
                int newAvg = 0;
                if (!rows.isEmpty()) {
                    int totalMinutes = 0;
                    for (DiaryProjectionDTO.SleepIntervalDTO r : rows) {
                        totalMinutes += (int) Duration.between(r.getSleepStartTime(), r.getSleepEndTime()).toMinutes();
                    }
                    newAvg = totalMinutes / rows.size();
                }

                // 평균 수면 시간 업데이트
                if (member.getAvgSleepTime() != newAvg) {
                    member.updateAvgSleepTime(newAvg);
                    updatedCount++;
                }
            }

            log.info("[스케줄러] 최근 7일 평균 수면시간 갱신 완료 | 대상: {}명", updatedCount);

        } catch (Exception e) {
            log.error("[스케줄러] 최근 7일 평균 수면시간 갱신 실패", e);
        }
    }

    /**
     * 정기 푸시알림 발송 메서드
     */
    public void sendRegularPushNotification() throws FirebaseMessagingException {
        log.info("Regular Push-Notification Start");
        List<String> targetFcmTokenList = pushRepository.findFcmTokenByAlarmTime(LocalDateTime.now().getHour());
        List<Message> messageList = targetFcmTokenList.stream()
                .map(fcmToken -> Message.builder()
                        .setToken(fcmToken)
                        .setApnsConfig(getApsConfigByContent(DEFAULT_CONTENT))
                        .setNotification(
                                Notification.builder()
                                        .setTitle(DEFAULT_TITLE)
                                        .setBody(DEFAULT_CONTENT)
                                        .build())
                        .build())
                .toList();

        BatchResponse response = FirebaseMessaging.getInstance().sendEach(messageList);

        log.info("Regular Push-Notification Complete - Success : {}, Failed : {}", response.getSuccessCount(), response.getFailureCount());

        log.info("Regular Push-Notification End");
    }

    /**
     *  연속 일기 미작성 날짜에 따른 푸시알림 발송 메서드
     */
    public void sendPushNotificationByContinuousMissedDays() throws FirebaseMessagingException {
        log.info("Missed Diary Date Push-Notification Start");
        // 알림 대상 추출 작업
        List<PushDataDTO.FcmTokenDateDiffDTO> targetFcmTokenListByDateDiff = pushRepository.findFcmTokenByLastDiaryDate();
        // 일기 연속 미작성 날짜에 따른 리스트 생성을 위한 작업
        Map<String, List<String>> fcmTokenMap = new HashMap<>();
        for (PushDataDTO.FcmTokenDateDiffDTO targetFcmTokenByDateDiff : targetFcmTokenListByDateDiff) {
            fcmTokenMap.computeIfAbsent(targetFcmTokenByDateDiff.getDateDifference(), k -> new ArrayList<>())
                    .add(targetFcmTokenByDateDiff.getFcmToken());
        }
        // 푸시알림을 위한 Message List 생성
        List<Message> messageListForThreeDaysAgo = getMessageListByDateDiff(
                fcmTokenMap.getOrDefault("three", Collections.emptyList()), THREE_DAYS_CONTENT);
        List<Message> messageListForFiveDaysAgo = getMessageListByDateDiff(
                fcmTokenMap.getOrDefault("five", Collections.emptyList()), FIVE_DAYS_CONTENT);
        List<Message> messageListForSevenDaysAgo = getMessageListByDateDiff(
                fcmTokenMap.getOrDefault("seven", Collections.emptyList()), SEVEN_DAYS_CONTENT);
        List<Message> messageListForFourteenDaysAgo = getMessageListByDateDiff(
                fcmTokenMap.getOrDefault("fourteen", Collections.emptyList()), FOURTEEN_DAYS_CONTENT);
        List<Message> messageListForThirtyDaysAgo = getMessageListByDateDiff(
                fcmTokenMap.getOrDefault("thirty", Collections.emptyList()), THIRTY_DAYS_CONTENT);

        BatchResponse responseByThree = FirebaseMessaging.getInstance().sendEach(messageListForThreeDaysAgo);
        BatchResponse responseByFive = FirebaseMessaging.getInstance().sendEach(messageListForFiveDaysAgo);
        BatchResponse responseBySeven = FirebaseMessaging.getInstance().sendEach(messageListForSevenDaysAgo);
        BatchResponse responseByFourteen = FirebaseMessaging.getInstance().sendEach(messageListForFourteenDaysAgo);
        BatchResponse responseByThirty = FirebaseMessaging.getInstance().sendEach(messageListForThirtyDaysAgo);

        log.info("Three Missed Diary Date - Success : {}, Failed : {}", responseByThree.getSuccessCount(), responseByThree.getFailureCount());
        log.info("Five Missed Diary Date - Success : {}, Failed : {}", responseByFive.getSuccessCount(), responseByFive.getFailureCount());
        log.info("Seven Missed Diary Date - Success : {}, Failed : {}", responseBySeven.getSuccessCount(), responseBySeven.getFailureCount());
        log.info("Fourteen Missed Diary Date - Success : {}, Failed : {}", responseByFourteen.getSuccessCount(), responseByFourteen.getFailureCount());
        log.info("Thirty Missed Diary Date - Success : {}, Failed : {}", responseByThirty.getSuccessCount(), responseByThirty.getFailureCount());

        log.info("Missed Diary Date Push-Notification End");
    }

    private List<Message> getMessageListByDateDiff (List<String> fcmTokenListThreeDaysAgo, String content) {
        return fcmTokenListThreeDaysAgo.stream()
                .map(fcmToken -> Message.builder()
                        .setToken(fcmToken)
                        .setApnsConfig(getApsConfigByContent(content))
                        .setNotification(
                                Notification.builder()
                                        .setTitle(DEFAULT_TITLE)
                                        .setBody(content)
                                        .build())
                        .build())
                .toList();
    }

    private ApnsConfig getApsConfigByContent (String content) {
        return ApnsConfig.builder()
                .setAps(Aps.builder()
                        .setAlert(
                                ApsAlert.builder()
                                        .setTitle(DEFAULT_TITLE)
                                        .setBody(content)
                                        .build()
                        )
                        .setSound(DEFAULT_SOUND)
                        .setBadge(DEFAULT_BADGE)
                        .build())
                .build();
    }
}
