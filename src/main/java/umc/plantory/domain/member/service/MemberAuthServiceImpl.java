package umc.plantory.domain.member.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.plantory.domain.member.converter.MemberConverter;
import umc.plantory.domain.member.dto.*;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.infra.KakaoAuthFacade;
import umc.plantory.domain.member.mapping.MemberTerm;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.member.repository.MemberTermRepository;
import umc.plantory.domain.term.entity.Term;
import umc.plantory.domain.term.repository.TermRepository;
import umc.plantory.global.enums.Provider;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.MemberHandler;
import umc.plantory.global.security.jwt.JwtProvider;
import umc.plantory.global.security.jwt.JwtResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 회원 인증/가입/약관 등 핵심 비즈니스 로직 담당함. 외부 연동은 퍼사드로 위임함
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberAuthServiceImpl implements MemberAuthService {
    private final KakaoAuthFacade kakaoAuthFacade;
    private final MemberRepository memberRepository;
    private final TermRepository termRepository;
    private final MemberTermRepository memberTermRepository;
    private final MemberConverter memberConverter;
    private final JwtProvider jwtProvider;

    /**
     * 카카오 로그인 처리 및 신규 회원이면 생성함
     * @param code 카카오 인가 코드
     * @return 인증 결과 DTO 반환함
     */
    @Override
    public MemberResponseDTO.MemberAuth kakaoLogin(String code) {
        KakaoTokenResponse token = kakaoAuthFacade.requestToken(code);
        KakaoUserInfo userInfo = kakaoAuthFacade.requestUserInfo(token.getAccessToken());

        Optional<Member> existing = memberRepository.findByProviderAndProviderId(Provider.KAKAO, String.valueOf(userInfo.getId()));
        Member member;
        boolean isNewUser = false;
        
        if (existing.isPresent()) {
            member = existing.get();
        } else {
            // 신규 유저 생성
            member = memberConverter.toNewMember(
                userInfo.getKakaoAccount().getEmail(), 
                String.valueOf(userInfo.getId()), 
                String.valueOf(userInfo.getId())
            );
            memberRepository.save(member);
            isNewUser = true;
        }

        return memberConverter.toMemberAuthResponse(
                member,
                token.getAccessToken(),
                token.getRefreshToken(),
                isNewUser
        );
    }

    /**
     * 카카오 로그인 + JWT 발급 한번에 해줌
     * @param code 카카오 인가 코드
     * @return JWT 토큰 DTO 반환함
     */
    @Override
    public JwtResponseDTO.Login kakaoLoginAndIssueJwt(String code) {
        MemberResponseDTO.MemberAuth auth = kakaoLogin(code);
        return kakaoAuthFacade.issueJwt(auth.getUserId());
    }

    /**
     * Authorization 헤더에서 memberId 추출해줌
     * @param authorizationHeader Bearer 토큰 헤더
     * @return memberId 반환함
     */
    @Override
    public Long extractMemberIdFromAuthorization(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }
        String accessToken = authorizationHeader.substring(7);
        return jwtProvider.getUserIdFromToken(accessToken);
    }

    /**
     * 약관 동의 정보 저장함
     * @param memberId 회원 PK
     * @param request 약관 동의 요청 DTO
     * @return 약관 동의 결과 반환함
     */
    @Transactional
    @Override
    public Map<String, Object> saveMemberAgreements(Long memberId, MemberRequestDTO.Agreement request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Map<String, Boolean> agreementMap = request.getTermAgreement();
        if (agreementMap == null || agreementMap.isEmpty()) {
            return memberConverter.toAgreementResponse(member);
        }

        // 각 약관 동의 정보 처리
        for (Map.Entry<String, Boolean> entry : agreementMap.entrySet()) {
            String termSort = entry.getKey();
            Boolean isAgree = entry.getValue();

            // Term 엔티티 조회
            Term term = termRepository.findByTermSort(termSort)
                    .orElseThrow(() -> new MemberHandler(ErrorStatus.TERM_NOT_FOUND));

            // 해당 회원과 약관에 대한 MemberTerm이 이미 존재하는지 확인
            memberTermRepository.findByMemberAndTerm(member, term)
                    .ifPresentOrElse(
                            // 이미 존재하면 동의 여부 업데이트
                            memberTerm -> memberTerm.updateIsAgree(isAgree),
                            // 존재하지 않으면 새로운 MemberTerm 생성 및 저장
                            () -> {
                                MemberTerm newMemberTerm = memberConverter.toMemberTerm(member, term, isAgree);
                                memberTermRepository.save(newMemberTerm);
                            }
                    );
        }
        
        return memberConverter.toAgreementResponse(member);
    }

    /**
     * 신규 회원 추가 정보 저장함
     * @param memberId 회원 PK
     * @param info 추가 정보 요청 DTO
     * @return 회원 프로필 DTO 반환함
     */
    @Transactional
    @Override
    public MemberResponseDTO.MemberProfile setInfoForNewMember(Long memberId, MemberRequestDTO.AdditionalInfo info) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        memberConverter.updateMemberInfo(member, info);
        memberRepository.save(member);
        
        return memberConverter.toMemberProfileResponse(member);
    }

    /**
     * memberId로 회원 조회함
     */
    @Override
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    /**
     * 닉네임 중복 체크함
     */
    @Override
    public boolean isNicknameExists(String nickname) {
        return memberRepository.findByNickname(nickname).isPresent();
    }

    /**
     * 필수 약관 동의 여부 검증함
     */
    @Override
    public boolean validateRequiredTerms(MemberRequestDTO.Agreement request) {
        Map<String, Boolean> agreementMap = request.getTermAgreement();
        if (agreementMap == null) return false;
        
        List<Term> requiredTerms = termRepository.findByIsRequiredTrue();
        for (Term term : requiredTerms) {
            if (!agreementMap.getOrDefault(term.getTermSort(), false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 추가 정보 필수 입력값 검증함
     */
    @Override
    public boolean validateAdditionalInfo(MemberRequestDTO.AdditionalInfo request) {
        return request.getNickname() != null && 
               request.getUserId() != null &&
               request.getGender() != null && 
               request.getBirth() != null;
    }
}
