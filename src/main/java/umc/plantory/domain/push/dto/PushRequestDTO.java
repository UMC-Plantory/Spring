package umc.plantory.domain.push.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PushRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PushNotificationTestRequest {
        String fcmToken;
        String title;
        String body;
        Integer badge;
    }
}
