package umc.plantory.domain.push.service;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import umc.plantory.domain.push.dto.PushRequestDTO;

@Service
@Slf4j
public class PushQueryService implements PushQueryUseCase{
    public String sendPushNotification(PushRequestDTO.PushNotificationRequest request) throws Exception {
        Aps aps = Aps.builder()
                .setAlert(
                        ApsAlert.builder()
                                .setTitle("플랜토리")
                                .setBody(request.getBody())
                                .build()
                )
                .setSound("default")
                .setBadge(1)
                .build();

        ApnsConfig apns = ApnsConfig.builder()
                .setAps(aps)
                .build();

        Message.Builder mb = Message.builder()
                .setToken(request.getFcmToken())
                .setApnsConfig(apns)
                .setNotification(
                        Notification.builder()
                                .setTitle("플랜토리")
                                .setBody(request.getBody())
                                .build()
                );

        return FirebaseMessaging.getInstance().send(mb.build());
    }
}
