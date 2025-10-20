package umc.plantory.domain.push.converter;

import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.push.entity.PushData;
import umc.plantory.global.enums.Platform;
import umc.plantory.global.enums.TokenStatus;

import java.time.LocalDateTime;

public class PushConverter {

    public static PushData toPushData(String fcmToken, Member member) {
        return PushData.builder()
                .fcmToken(fcmToken)
                .member(member)
                .lastSeenAt(LocalDateTime.now())
                .platform(Platform.IOS)
                .tokenStatus(TokenStatus.VALID)
                .build();
    }
}
