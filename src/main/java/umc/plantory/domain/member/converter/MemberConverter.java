package umc.plantory.domain.member.converter;

import org.springframework.stereotype.Component;
import umc.plantory.domain.member.dto.MemberRequestDTO;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.mapping.MemberTerm;
import umc.plantory.domain.term.entity.Term;
import umc.plantory.global.enums.Gender;
import umc.plantory.global.enums.MemberRole;
import umc.plantory.global.enums.MemberStatus;
import umc.plantory.global.enums.Provider;

import java.util.Map;

@Component
public class MemberConverter {

    private static final String TEMP_NICKNAME_PREFIX = "새싹이";
    // S3로 수정 예정
    private static final String DEFAULT_PROFILE_IMAGE_URL = "/images/default_profile.png";

    // 신규 회원 엔티티 생성
    public Member toNewMember(String email, String providerId, String kakaoId) {
        return Member.builder()
                .email(email)
                .nickname(TEMP_NICKNAME_PREFIX + System.currentTimeMillis())
                .profileImgUrl(DEFAULT_PROFILE_IMAGE_URL)
                .provider(Provider.KAKAO)
                .providerId(providerId)
                .role(MemberRole.USER)
                .status(MemberStatus.ACTIVE)
                .userId("kakao_" + kakaoId)
                .build();
    }

    // 회원 정보 업데이트
    public void updateMemberInfo(Member member, MemberRequestDTO.AdditionalInfo request) {
        member.updateNickname(request.getNickname());
        member.updateUserId(request.getUserId());
        member.updateGender(Gender.valueOf(request.getGender()));
        member.updateBirth(request.getBirth());
    }

    // AuthResponse 생성
    public MemberResponseDTO.MemberAuth toMemberAuthResponse(Member member, String accessToken, String refreshToken, boolean isNewUser) {
        return MemberResponseDTO.MemberAuth.builder()
                .userId(member.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNewUser(isNewUser)
                .build();
    }

    // MemberTerm 엔티티 생성
    public MemberTerm toMemberTerm(Member member, Term term, boolean isAgree) {
        return MemberTerm.builder()
                .member(member)
                .term(term)
                .isAgree(isAgree)
                .build();
    }

    // 회원 프로필 응답 DTO 생성
    public MemberResponseDTO.MemberProfile toMemberProfileResponse(Member member) {
        return MemberResponseDTO.MemberProfile.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .gender(member.getGender() != null ? member.getGender().toString().toLowerCase() : null)
                .birth(member.getBirth() != null ? member.getBirth().toString() : null)
                .profileImgUrl(member.getProfileImgUrl())
                .build();
    }

    // 약관 동의 정보 응답 DTO 생성
    public Map<String, Object> toAgreementResponse(Member member) {
        return Map.of("email", member.getEmail());
    }
} 