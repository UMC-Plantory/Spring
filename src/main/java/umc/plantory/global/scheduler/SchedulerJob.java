package umc.plantory.global.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.diary.entity.DiaryImg;
import umc.plantory.domain.diary.repository.DiaryImgRepository;
import umc.plantory.domain.diary.repository.DiaryRepository;
import umc.plantory.domain.image.service.ImageUseCase;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.wateringCan.entity.WateringCan;
import umc.plantory.domain.wateringCan.repository.WateringCanRepository;
import umc.plantory.global.enums.DiaryStatus;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerJob {

    private final DiaryRepository diaryRepository;
    private final DiaryImgRepository diaryImgRepository;
    private final WateringCanRepository wateringCanRepository;
    private final MemberRepository memberRepository;
    private final ImageUseCase imageUseCase;

    /**
     * 매일 자정 00:00에 TEMP 상태 중 30일 지난 일기를 DELETE 상태로 변경
     */
    @Transactional
    public void updateTempToDeleted() {
        LocalDateTime start = LocalDateTime.now();
        log.info("[스케줄러] TEMP → DELETE 작업 시작");

        try {
            // 30일전 임시 보관된 TEMP 상태의 일기 조회
            LocalDate threshold = LocalDate.now().minusDays(30);
            List<Diary> tempDiaries = diaryRepository
                    .findByStatusAndTempSavedAtBefore(DiaryStatus.TEMP, threshold.atStartOfDay());

            // DELETE 상태로 바꾸고 deletedAt 기록
            for (Diary diary : tempDiaries) {
                diary.updateStatus(DiaryStatus.DELETE);
                diary.updateDeletedAt(start);
            }

            Duration duration = Duration.between(start, LocalDateTime.now());
            log.info("[스케줄러] TEMP → DELETE 처리 완료 | 대상: {}건 | 소요 시간: {}ms",
                    tempDiaries.size(), duration.toMillis());
        } catch (Exception e) {
            log.error("[스케줄러] TEMP → DELETE 처리 실패", e);
        }
    }

    /**
     * 매일 자정 00:00에 DELETE 상태 중 30일 지난 일기를 영구 삭제 처리
     */
    @Transactional
    public void deleteDiariesPermanently() {
        LocalDateTime start = LocalDateTime.now();
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

            Duration duration = Duration.between(start, LocalDateTime.now());
            log.info("[스케줄러] DELETE → 영구 삭제 처리 완료 | 대상: {}건 | 소요 시간: {}ms",
                    deletedDiaries.size(), duration.toMillis());
        } catch (Exception e) {
            log.error("[스케줄러] DELETE → 영구 삭제 처리 실패", e);
        }
    }

    /**
     * 매일 자정 00:00에 어제 일기를 작성하지 않은 멤버를 찾아 continuousRecordCnt 0으로 초기화
     */
    @Transactional
    public void resetContinuousRecordCnt() {
        LocalDateTime start = LocalDateTime.now();
        log.info("[스케줄러] 연속 기록 초기화 시작");

        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            long updated = memberRepository.resetStreak(yesterday);

            Duration duration = Duration.between(start, LocalDateTime.now());
            log.info("[스케줄러] 연속 기록 초기화 완료 | 대상: {}건 | 소요 시간: {}ms", updated, duration.toMillis());
        } catch (Exception e) {
            log.error("[스케줄러] 연속 기록 초기화 실패", e);
        }
    }
}
