package umc.plantory.domain.member.converter;

import umc.plantory.domain.member.dto.MemberDataDTO;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.global.enums.Gender;
import umc.plantory.global.enums.MemberRole;
import umc.plantory.global.enums.MemberStatus;
import umc.plantory.global.enums.Provider;
import umc.plantory.domain.member.mapping.MemberTerm;
import umc.plantory.domain.term.entity.Term;

public class MemberConverter {
    private static final String DEFAULT_PROFILE_IMG_URL = "https://plantory.s3.ap-northeast-2.amazonaws.com/profile/plantory_default_img.png";
    private static final String DEFAULT_NICKNAME = "토리";
    private static final String DEFAULT_USER_CUSTOM_ID = "temp_plantory";

    public static MemberResponseDTO.TermAgreementResponse toTermAgreementResponse(Member member) {
        return MemberResponseDTO.TermAgreementResponse.builder()
                .memberId(member.getId())
                .message("약관 동의가 완료되었습니다.")
                .build();
    }

    public static MemberResponseDTO.MemberSignupResponse toMemberSignupResponse(Member member) {
        return MemberResponseDTO.MemberSignupResponse.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .userCustomId(member.getUserCustomId())
                .profileImgUrl(member.getProfileImgUrl() != null ? member.getProfileImgUrl() : DEFAULT_PROFILE_IMG_URL)
                .build();
    }

    public static MemberTerm toMemberTerm(Member member, Term term, Boolean isAgree) {
        return MemberTerm.builder()
                .member(member)
                .term(term)
                .isAgree(isAgree)
                .build();
    }

    public static Member toMember (MemberDataDTO.KakaoMemberData kakaoMemberData) {
        return Member.builder()
                .nickname(DEFAULT_NICKNAME)
                .email(kakaoMemberData.getEmail())
                .userCustomId(DEFAULT_USER_CUSTOM_ID)
                .profileImgUrl(DEFAULT_PROFILE_IMG_URL)
                .provider(Provider.KAKAO)
                .providerId(kakaoMemberData.getSub())
                .gender(Gender.NONE)
                .status(MemberStatus.PENDING)
                .role(MemberRole.USER)
                .build();
    }
}
