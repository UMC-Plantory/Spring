package umc.plantory.global.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private final SchedulerJob schedulerJob;

    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void executeScheduledTasks() {
        schedulerJob.updateTempToDeleted();
        schedulerJob.deleteDiariesPermanently();
        schedulerJob.resetContinuousRecordCnt();
    }
}