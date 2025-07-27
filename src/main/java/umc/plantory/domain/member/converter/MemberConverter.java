package umc.plantory.domain.member.converter;

import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.mapping.MemberTerm;
import umc.plantory.domain.term.entity.Term;

public class MemberConverter {
    private static final String tempNickname = "새싹이";
    private static final String defaultProfileImg = "";

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
                .userCustomId(member.getUserId())
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
