package umc.plantory.domain.diary.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.diary.entity.DiaryImg;
import umc.plantory.domain.diary.repository.DiaryImgRepository;
import umc.plantory.domain.diary.repository.DiaryRepository;
import umc.plantory.domain.image.service.ImageUseCase;
import umc.plantory.domain.wateringCan.entity.WateringCan;
import umc.plantory.domain.wateringCan.repository.WateringCanRepository;
import umc.plantory.global.enums.DiaryStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiaryCleanupScheduler {

    private final DiaryRepository diaryRepository;
    private final DiaryImgRepository diaryImgRepository;
    private final WateringCanRepository wateringCanRepository;
    private final ImageUseCase imageUseCase;

    /**
     * 매일 자정 00:00에 TEMP 상태 중 30일 지난 일기를 DELETE 상태로 변경
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateTempToDeleted() {
        LocalDateTime start = LocalDateTime.now();

        // 30일전 임시 보관된 TEMP 상태의 일기 조회
        LocalDateTime threshold = start.minusDays(30);
        List<Diary> tempDiaries = diaryRepository.findByStatusAndTempSavedAtBefore(DiaryStatus.TEMP, threshold);

        // DELETE 상태로 바꾸고 deletedAt 기록
        for (Diary diary : tempDiaries) {
            diary.updateStatus(DiaryStatus.DELETE);
            diary.updateDeletedAt(start);
        }

        Duration duration = Duration.between(start, LocalDateTime.now());
        log.info("[스케줄러] TEMP → DELETE 처리 완료 | 대상: {}건 | 소요 시간: {}ms",
                tempDiaries.size(), duration.toMillis());
    }

    /**
     * 매일 자정 00:00에 DELETE 상태 중 30일 지난 일기를 영구 삭제 처리
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deleteDiariesPermanently() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime threshold = start.minusDays(30);

        // 30일전 휴지통으로 이동한 DELETE 상태의 일기 조회
        List<Diary> deletedDiaries = diaryRepository.findByStatusAndDeletedAtBefore(DiaryStatus.DELETE, threshold);

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
    }
}