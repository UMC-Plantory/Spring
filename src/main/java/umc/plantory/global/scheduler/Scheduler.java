package umc.plantory.global.scheduler;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduler {

    private final SchedulerJob schedulerJob;

    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void executeScheduledTasks() throws FirebaseMessagingException {
        schedulerJob.resetContinuousRecordCnt();
        schedulerJob.updateTempToDeleted();
        schedulerJob.deleteDiariesPermanently();
        schedulerJob.updateAvgSleepTime();
        schedulerJob.sendPushNotificationByContinuousMissedDays();
    }

    // 매시간마다 실행
    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    public void sendIosPushNotification() throws FirebaseMessagingException {
        log.info("Push-Notification Scheduler On");
        schedulerJob.sendRegularPushNotification();

        log.info("Push-Notification Scheduler Off");
    }
}