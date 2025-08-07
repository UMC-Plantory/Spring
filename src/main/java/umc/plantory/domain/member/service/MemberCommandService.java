package umc.plantory.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.flower.repository.FlowerRepository;
import umc.plantory.domain.member.converter.MemberConverter;
import umc.plantory.domain.member.dto.MemberDataDTO;
import umc.plantory.domain.member.dto.MemberRequestDTO;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.mapping.MemberTerm;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.member.repository.MemberTermRepository;
import umc.plantory.domain.term.repository.TermRepository;
import umc.plantory.domain.terrarium.converter.TerrariumConverter;
import umc.plantory.domain.terrarium.entity.Terrarium;
import umc.plantory.domain.terrarium.repository.TerrariumRepository;
import umc.plantory.domain.token.provider.JwtProvider;
import umc.plantory.domain.token.repository.MemberTokenRepository;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.MemberHandler;
import umc.plantory.global.apiPayload.exception.handler.TermHandler;
import umc.plantory.global.enums.Emotion;
import umc.plantory.global.enums.MemberStatus;

import java.util.List;
import umc.plantory.domain.term.entity.Term;

@Service
@RequiredArgsConstructor
public class MemberCommandService implements MemberCommandUseCase {
    private final MemberRepository memberRepository;
    private final MemberTermRepository memberTermRepository;
    private final TermRepository termRepository;
    private final JwtProvider jwtProvider;
    private final MemberTokenRepository memberTokenRepository;
    private final TerrariumRepository terrariumRepository;
    private final FlowerRepository flowerRepository;

    private static final String DEFAULT_PROFILE_IMG_URL = "https://plantory.s3.ap-northeast-2.amazonaws.com/profile/plantory_default_img.png";

    @Override
    @Transactional
    public MemberResponseDTO.TermAgreementResponse termAgreement(String authorization, MemberRequestDTO.TermAgreementRequest request) {
        // Authorization 헤더에서 토큰 추출
        String token = jwtProvider.resolveToken(authorization);
        if (token == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        // JWT 토큰 검증 및 멤버 ID 추출
        jwtProvider.validateToken(token);
        Long memberId = jwtProvider.getMemberId(token);

        // 회원 조회
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 약관 동의 정보 저장
        List<Long> agreeTermIdList = request.getAgreeTermIdList();
        List<Long> disagreeTermIdList = request.getDisagreeTermIdList();

        if (!validateRequiredTerms(agreeTermIdList)) {
            throw new TermHandler(ErrorStatus.REQUIRED_TERM_NOT_AGREED);
        }
        // 동의한 약관
        for (Long termId : agreeTermIdList) {
            Term term = termRepository.findById(termId)
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
            Term term = termRepository.findById(termId)
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
        return MemberConverter.toTermAgreementResponse(findMember);
    }

    @Override
    @Transactional
    public MemberResponseDTO.MemberSignupResponse memberSignup(String authorization, MemberRequestDTO.MemberSignupRequest request) {
        // Authorization 헤더에서 토큰 추출
        String token = jwtProvider.resolveToken(authorization);
        if (token == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        // JWT 토큰 검증 및 멤버 ID 추출
        jwtProvider.validateToken(token);
        Long memberId = jwtProvider.getMemberId(token);

        // 회원 조회
        Member findMember = memberRepository.findById(memberId)
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

        // 프로필 이미지 설정
        String profileImgUrl = request.getProfileImgUrl();
        if (profileImgUrl != null && !profileImgUrl.trim().isEmpty()) {
            findMember.updateProfileImgUrl(profileImgUrl);
        } else {
            // 기본 프로필 이미지 설정
            findMember.updateProfileImgUrl(DEFAULT_PROFILE_IMG_URL);
        }

        memberRepository.save(findMember);

        // 초기 테라리움 생성
        terrariumRepository.save(TerrariumConverter.toTerrarium(findMember,
                flowerRepository.findByEmotion(Emotion.DEFAULT)));

        // 응답 반환
        return MemberConverter.toMemberSignupResponse(findMember);
    }

    @Override
    @Transactional
    public MemberResponseDTO.ProfileUpdateResponse updateProfile(String authorization, MemberRequestDTO.ProfileUpdateRequest request) {
        // Authorization 헤더에서 토큰 추출
        String token = jwtProvider.resolveToken(authorization);
        if (token == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        // JWT 토큰 검증 및 멤버 ID 추출
        jwtProvider.validateToken(token);
        Long memberId = jwtProvider.getMemberId(token);

        // 회원 조회
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 프로필 정보 업데이트
        if (request.getNickname() != null) {
            findMember.updateNickname(request.getNickname());
        }
        if (request.getUserCustomId() != null) {
            findMember.updateUserCustomId(request.getUserCustomId());
        }
        if (request.getGender() != null) {
            findMember.updateGender(request.getGender());
        }
        if (request.getBirth() != null) {
            findMember.updateBirth(request.getBirth());
        }

        // 프로필 이미지 처리
        if (request.getDeleteProfileImg() != null && request.getDeleteProfileImg()) {
            // 이미지 삭제 요청이면 기본 이미지로 설정
            findMember.updateProfileImgUrl(DEFAULT_PROFILE_IMG_URL);
        } else if (request.getProfileImgUrl() != null) {
            // 새로운 이미지 URL이 제공되면 업데이트
            findMember.updateProfileImgUrl(request.getProfileImgUrl());
        }

        memberRepository.save(findMember);

        // 응답 반환
        return MemberConverter.toProfileUpdateResponse(findMember);
    }
  
    @Override
    @Transactional
    public void logout(String authorization) {
        // Authorization 헤더에서 토큰 추출
        String token = jwtProvider.resolveToken(authorization);
        if (token == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        // JWT 토큰 검증 및 멤버 ID 추출
        jwtProvider.validateToken(token);
        Long memberId = jwtProvider.getMemberId(token);

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 해당 멤버의 토큰 정보 삭제
        memberTokenRepository.deleteByMember(member);
    }

    @Override
    @Transactional
    public void delete(String authorization) {
        // Authorization 헤더에서 토큰 추출
        String token = jwtProvider.resolveToken(authorization);
        if (token == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        // JWT 토큰 검증 및 멤버 ID 추출
        jwtProvider.validateToken(token);
        Long memberId = jwtProvider.getMemberId(token);

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // soft delete: status를 INACTIVE로 변경하고 inactiveAt 설정
        member.updateStatus(MemberStatus.INACTIVE);
        
        // 해당 멤버의 토큰 정보 삭제
        memberTokenRepository.deleteByMember(member);
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

    /**
     * id_token 에서 추출한 멤버 데이터를 통해
     * 기존 회원이라면 조회해서 가져오고
     * 첫 로그인이면 데이터 저장하는 메서드
     * @param kakaoMemberData : id_token 에서 추출한 멤버 데이터
     * @return : 추출한 멤버 데이터와 일치하는 멤버
     */
    @Override
    @Transactional
    public Member findOrCreateMember(MemberDataDTO.KakaoMemberData kakaoMemberData) {
        Member findOrNewMember = memberRepository.findByProviderId(kakaoMemberData.getSub())
                .orElseGet(() -> MemberConverter.toMember(kakaoMemberData));

        return memberRepository.save(findOrNewMember);
    }
}
