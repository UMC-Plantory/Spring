package umc.plantory.domain.push.service;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import umc.plantory.domain.push.dto.PushRequestDTO;

@Service
@Slf4j
public class PushQueryService implements PushQueryUseCase{
    public String sendIosAlertTest(PushRequestDTO.PushNotificationTestRequest request) throws Exception {
        log.error("on");

        Aps aps = Aps.builder()
                .setAlert(
                        ApsAlert.builder()
                                .setTitle(request.getTitle())
                                .setBody(request.getBody())
                                .build()
                )
                .setSound("default")
                .setBadge(request.getBadge())
                .build();

        ApnsConfig apns = ApnsConfig.builder()
                .setAps(aps)
                .build();

        Message.Builder mb = Message.builder()
                .setToken(request.getFcmToken())
                .setApnsConfig(apns)
                .setNotification(
                        Notification.builder()
                                .setTitle(request.getTitle())
                                .setBody(request.getBody())
                                .build()
                );

        return FirebaseMessaging.getInstance().send(mb.build());
    }
}
