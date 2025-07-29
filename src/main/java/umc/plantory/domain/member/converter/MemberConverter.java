package umc.plantory.domain.member.converter;

import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.mapping.MemberTerm;
import umc.plantory.domain.term.entity.Term;

public class MemberConverter {
    private static final String tempNickname = "토리";
    private static final String defaultProfileImg = "https://plantory.s3.ap-northeast-2.amazonaws.com/profile/plantory_default_img.png";

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
                .build();
    }

    public static MemberResponseDTO.ProfileResponse toProfileResponse(Member member) {
        return MemberResponseDTO.ProfileResponse.builder()
                .userCustomId(member.getUserCustomId())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .gender(member.getGender() != null ? member.getGender().name().toLowerCase() : null)
                .birth(member.getBirth() != null ? member.getBirth().toString() : null)
                .profileImgUrl(member.getProfileImgUrl())
                .wateringCanCnt(member.getWateringCanCnt())
                .continuousRecordCnt(member.getContinuousRecordCnt())
                .totalRecordCnt(member.getTotalRecordCnt())
                .avgSleepTime(member.getAvgSleepTime()) // 분단위 수면 시간
                .totalBloomCnt(member.getTotalBloomCnt())
                .status(member.getStatus() != null ? member.getStatus().name() : null)
                .build();
    }

    public static MemberTerm toMemberTerm(Member member, Term term, Boolean isAgree) {
        return MemberTerm.builder()
                .member(member)
                .term(term)
                .isAgree(isAgree)
                .build();
    }
}