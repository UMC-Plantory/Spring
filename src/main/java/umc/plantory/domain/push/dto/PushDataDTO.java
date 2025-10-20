package umc.plantory.domain.push.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PushDataDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FcmTokenDateDiffDTO {
        private String fcmToken;
        private String dateDifference;
    }
}
