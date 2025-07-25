package umc.plantory.domain.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.plantory.domain.member.converter.MemberConverter;
import umc.plantory.domain.member.dto.MemberRequestDTO;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.mapping.MemberTerm;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.member.repository.MemberTermRepository;
import umc.plantory.domain.term.repository.TermRepository;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.MemberHandler;
import umc.plantory.global.apiPayload.exception.handler.TermHandler;

import java.util.List;
import umc.plantory.domain.term.entity.Term;

@Service
@RequiredArgsConstructor
public class MemberCommandService implements MemberCommandUseCase{
    private final MemberRepository memberRepository;
    private final MemberTermRepository memberTermRepository;
    private final TermRepository termRepository;

    @Override
    @Transactional
    public MemberResponseDTO.MemberSignupResponse memberSignup(MemberRequestDTO.MemberSignupRequest request) {
        // 회원 조회
        Member findMember = memberRepository.findById(request.getMemberId())
            .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 필수 추가 정보 검증
        if (!validateAdditionalInfo(request)) {
            throw new MemberHandler(ErrorStatus.INVALID_MEMBER_INFO);
        }

        // 추가 정보 저장
        findMember.updateNickname(request.getNickname());
        findMember.updateUserCustomId(request.getUserCustomId());
        findMember.updateBirth(request.getBirth());
        findMember.updateGender(request.getGender());
        memberRepository.save(findMember);

        // 약관 동의 정보 저장
        List<Long> agreeTermIdList = request.getAgreeTermIdList();
        List<Long> disagreeTermIdList = request.getDisagreeTermIdList();
        if (!validateRequiredTerms(agreeTermIdList)) {
            throw new TermHandler(ErrorStatus.REQUIRED_TERM_NOT_AGREED);
        }
        // 동의한 약관
        for (Long termId : agreeTermIdList) {
            var term = termRepository.findById(termId)
                .orElseThrow(() -> new TermHandler(ErrorStatus.TERM_NOT_FOUND));
            memberTermRepository.findByMemberAndTerm(findMember, term)
                .ifPresentOrElse(
                    mt -> mt.updateIsAgree(true),
                    () -> {
                        MemberTerm newMemberTerm = MemberConverter.toMemberTerm(findMember, term, true);
                        memberTermRepository.save(newMemberTerm);
                    }
                );
        }
        // 미동의한 약관
        for (Long termId : disagreeTermIdList) {
            var term = termRepository.findById(termId)
                .orElseThrow(() -> new TermHandler(ErrorStatus.TERM_NOT_FOUND));
            memberTermRepository.findByMemberAndTerm(findMember, term)
                .ifPresentOrElse(
                    mt -> mt.updateIsAgree(false),
                    () -> {
                        MemberTerm newMemberTerm = MemberConverter.toMemberTerm(findMember, term, false);
                        memberTermRepository.save(newMemberTerm);
                    }
                );
        }
        // 응답 반환
        return MemberConverter.toMemberSignupResponse(findMember);
    }

    // 추가 정보 필수 입력값 검증
    private boolean validateAdditionalInfo(MemberRequestDTO.MemberSignupRequest request) {
        return request.getNickname() != null &&
               request.getUserCustomId() != null &&
               request.getGender() != null &&
               request.getBirth() != null;
    }

    // 필수 약관 동의 여부 검증
    private boolean validateRequiredTerms(List<Long> agreeTermIdList) {
        if (agreeTermIdList == null) return false;
        List<Term> requiredTerms = termRepository.findByIsRequiredTrue();
        for (Term term : requiredTerms) {
            if (!agreeTermIdList.contains(term.getId())) {
                return false;
            }
        }
        return true;
    }
}
