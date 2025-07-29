package umc.plantory.domain.member.converter;

import umc.plantory.domain.member.dto.MemberDataDTO;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.global.enums.Gender;
import umc.plantory.global.enums.MemberRole;
import umc.plantory.global.enums.MemberStatus;
import umc.plantory.global.enums.Provider;

public class MemberConverter {
    private static final String DEFAULT_NICKNAME = "토리";
    private static final String DEFAULT_PROFILE_IMG = "https://plantory.s3.ap-northeast-2.amazonaws.com/profile/plantory_default_img.png";

    public static Member toMember (MemberDataDTO.KakaoMemberData kakaoMemberData) {
        return Member.builder()
                .nickname(DEFAULT_NICKNAME)
                .email(kakaoMemberData.getEmail())
                .userCustomId("")
                .profileImgUrl(DEFAULT_PROFILE_IMG)
                .provider(Provider.KAKAO)
                .providerId(kakaoMemberData.getSub())
                .gender(Gender.NONE)
                .status(MemberStatus.PENDING)
                .role(MemberRole.USER)
                .build();
    }

}
