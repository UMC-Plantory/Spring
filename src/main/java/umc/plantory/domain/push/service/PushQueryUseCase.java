package umc.plantory.domain.push.service;

import umc.plantory.domain.push.dto.PushRequestDTO;

public interface PushQueryUseCase {
    public String sendPushNotification(PushRequestDTO.PushNotificationRequest request) throws Exception;
}
