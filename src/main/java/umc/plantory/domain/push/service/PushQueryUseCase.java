package umc.plantory.domain.push.service;

import umc.plantory.domain.push.dto.PushRequestDTO;

public interface PushQueryUseCase {
    public String sendIosAlertTest(PushRequestDTO.PushNotificationTestRequest request) throws Exception;
}
