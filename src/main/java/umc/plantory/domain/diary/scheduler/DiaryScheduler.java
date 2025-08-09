package umc.plantory.domain.diary.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiaryScheduler {

    private final DiarySchedulerJob diarySchedulerJob;

    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void executeScheduledTasks() {
        diarySchedulerJob.updateTempToDeleted();
        diarySchedulerJob.deleteDiariesPermanently();
    }
}