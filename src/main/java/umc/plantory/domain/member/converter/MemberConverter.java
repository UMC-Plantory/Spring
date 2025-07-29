package umc.plantory.domain.member.converter;

import umc.plantory.domain.member.dto.MemberDataDTO;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.global.enums.Gender;
import umc.plantory.global.enums.MemberRole;
import umc.plantory.global.enums.MemberStatus;
import umc.plantory.global.enums.Provider;

public class MemberConverter {
    private static final String defaultNickname = "토리";
    private static final String defaultProfileImg = "";

    public static Member toMember (MemberDataDTO.KakaoMemberData kakaoMemberData) {
        return Member.builder()
                .nickname(defaultNickname)
                .email(kakaoMemberData.getEmail())
                .userCustomId("")
                .profileImgUrl(defaultProfileImg)
                .provider(Provider.KAKAO)
                .providerId(kakaoMemberData.getSub())
                .gender(Gender.NONE)
                .status(MemberStatus.PENDING)
                .role(MemberRole.USER)
                .build();
    }

}
