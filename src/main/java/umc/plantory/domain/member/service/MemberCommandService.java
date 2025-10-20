package umc.plantory.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.chat.repository.ChatRepository;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.diary.repository.DiaryImgRepository;
import umc.plantory.domain.diary.repository.DiaryRepository;
import umc.plantory.domain.flower.entity.Flower;
import umc.plantory.domain.flower.repository.FlowerRepository;
import umc.plantory.domain.kakao.service.KakaoOidcService;
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
import umc.plantory.domain.wateringCan.repository.WateringCanRepository;
import umc.plantory.domain.wateringCan.repository.WateringEventRepository;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.MemberHandler;
import umc.plantory.global.apiPayload.exception.handler.TermHandler;
import umc.plantory.global.enums.Emotion;
import umc.plantory.global.enums.MemberStatus;

import java.util.List;
import umc.plantory.domain.term.entity.Term;
import umc.plantory.global.enums.Provider;

@Slf4j
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
    private final ChatRepository chatRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryImgRepository diaryImgRepository;
    private final WateringEventRepository wateringEventRepository;
    private final WateringCanRepository wateringCanRepository;

    private final KakaoOidcService kakaoOidcService;

    private static final String DEFAULT_PROFILE_IMG_URL = "https://plantory.s3.ap-northeast-2.amazonaws.com/profile/plantory_default_img.png";

    @Override
    @Transactional
    public MemberResponseDTO.TermAgreementResponse termAgreement(String authorization, MemberRequestDTO.TermAgreementRequest request) {
        // JWT 토큰 검증 및 멤버 ID 추출
        Long memberId = jwtProvider.getMemberIdAndValidateToken(authorization);

        // 회원 조회
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 약관 동의 처리
        processTermAgreement(findMember, request);

        // 회원 상태 AGREE로 변경
        findMember.updateStatus(MemberStatus.AGREE);
        memberRepository.save(findMember);

        // 응답 반환
        return MemberConverter.toTermAgreementResponse(findMember);
    }

    /**
     * 약관 동의 처리를 담당하는 메서드
     */
    private void processTermAgreement(Member member, MemberRequestDTO.TermAgreementRequest request) {
        List<Long> agreeTermIdList = request.getAgreeTermIdList();
        List<Long> disagreeTermIdList = request.getDisagreeTermIdList();

        // 필수 약관 동의 검증
        if (!validateRequiredTerms(agreeTermIdList)) {
            throw new TermHandler(ErrorStatus.REQUIRED_TERM_NOT_AGREED);
        }

        // 동의한 약관 처리
        processAgreedTerms(member, agreeTermIdList);

        // 미동의한 약관 처리
        processDisagreedTerms(member, disagreeTermIdList);
    }

    /**
     * 동의한 약관들을 처리하는 메서드
     */
    private void processAgreedTerms(Member member, List<Long> agreeTermIdList) {
        for (Long termId : agreeTermIdList) {
            Term term = getTermOrThrow(termId);
            updateMemberTermAgreement(member, term, true);
        }
    }

    /**
     * 미동의한 약관들을 처리하는 메서드
     */
    private void processDisagreedTerms(Member member, List<Long> disagreeTermIdList) {
        // null 처리
        if (disagreeTermIdList == null) return;
        for (Long termId : disagreeTermIdList) {
            Term term = getTermOrThrow(termId);
            updateMemberTermAgreement(member, term, false);
        }
    }

    /**
     * 멤버-약관 동의 상태를 업데이트하는 메서드
     */
    private void updateMemberTermAgreement(Member member, Term term, boolean isAgree) {
        memberTermRepository.findByMemberAndTerm(member, term)
                .ifPresentOrElse(
                        mt -> mt.updateIsAgree(isAgree),
                        () -> {
                            MemberTerm newMemberTerm = MemberConverter.toMemberTerm(member, term, isAgree);
                            memberTermRepository.save(newMemberTerm);
                        }
                );
    }

    /**
     * 약관을 조회하거나 예외를 발생시키는 메서드
     */
    private Term getTermOrThrow(Long termId) {
        return termRepository.findById(termId)
                .orElseThrow(() -> new TermHandler(ErrorStatus.TERM_NOT_FOUND));
    }

    @Override
    @Transactional
    public MemberResponseDTO.MemberSignupResponse memberSignup(String authorization, MemberRequestDTO.MemberSignupRequest request) {
        // JWT 토큰 검증 및 멤버 ID 추출
        Long memberId = jwtProvider.getMemberIdAndValidateToken(authorization);

        // 회원 조회
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 회원가입 처리
        processMemberSignup(findMember, request);

        // 응답 반환
        return MemberConverter.toMemberSignupResponse(findMember);
    }

    /**
     * 회원가입 처리를 담당하는 메서드
     * 단일책임원칙에 따라 회원가입 로직을 분리
     */
    private void processMemberSignup(Member member, MemberRequestDTO.MemberSignupRequest request) {
        // 필수 추가 정보 검증
        if (!validateAdditionalInfo(request)) {
            throw new MemberHandler(ErrorStatus.INVALID_MEMBER_INFO);
        }

        // 추가 정보 저장
        updateMemberInfo(member, request);

        // 회원 상태 ACTIVE로 변경
        member.updateStatus(MemberStatus.ACTIVE);
        memberRepository.save(member);
    }

    /**
     * 회원 정보를 업데이트하는 메서드
     */
    private void updateMemberInfo(Member member, MemberRequestDTO.MemberSignupRequest request) {
        member.updateNickname(request.getNickname());
        member.updateUserCustomId(request.getUserCustomId());
        member.updateBirth(request.getBirth());
        member.updateGender(request.getGender());

        // 프로필 이미지 설정
        String profileImgUrl = request.getProfileImgUrl();
        if (profileImgUrl != null && !profileImgUrl.trim().isEmpty()) {
            member.updateProfileImgUrl(profileImgUrl);
        } else {
            // 기본 프로필 이미지 설정
            member.updateProfileImgUrl(DEFAULT_PROFILE_IMG_URL);
        }
    }

    @Override
    @Transactional
    public MemberResponseDTO.ProfileUpdateResponse updateProfile(String authorization, MemberRequestDTO.ProfileUpdateRequest request) {
        // JWT 토큰 검증 및 멤버 ID 추출
        Long memberId = jwtProvider.getMemberIdAndValidateToken(authorization);

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
        // JWT 토큰 검증 및 멤버 ID 추출
        Long memberId = jwtProvider.getMemberIdAndValidateToken(authorization);

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 해당 멤버의 토큰 정보 삭제
        memberTokenRepository.deleteByMember(member);
    }

    @Override
    @Transactional
    public void delete(String authorization) {
        // JWT 토큰 검증 및 멤버 ID 추출
        Long memberId = jwtProvider.getMemberIdAndValidateToken(authorization);

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

//        // soft delete: status를 INACTIVE로 변경하고 inactiveAt 설정
//        member.updateStatus(MemberStatus.INACTIVE);

        // hard delete
        List<Diary> diaryList = diaryRepository.findAllByMember(member);
        List<Terrarium> terrariumList = terrariumRepository.findAllByMember(member);

        chatRepository.deleteAllByMember(member);
        memberTermRepository.deleteAllByMember(member);
        diaryImgRepository.deleteAllByDiaryIn(diaryList);
        wateringEventRepository.deleteAllByTerrariumIn(terrariumList);
        terrariumRepository.deleteAllByMember(member);
        wateringCanRepository.deleteAllByMember(member);
        diaryRepository.deleteAllByMember(member);
        memberTokenRepository.deleteByMember(member);

        if (member.getProvider().equals(Provider.KAKAO)) {
            // 카카오 연동 해제
            kakaoOidcService.unlinkUser(member.getProviderId());
        } else {
            // 애플 연동 해제

        }

        memberRepository.delete(member);
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
     * @param memberData : id_token 에서 추출한 멤버 데이터
     * @return : 추출한 멤버 데이터와 일치하는 멤버
     */
    @Override
    @Transactional
    public Member findOrCreateMember(MemberDataDTO.MemberData memberData, Provider provider) {
        return memberRepository.findByProviderId(memberData.getSub())
                .orElseGet(() -> {
                    // 새 멤버 생성
                    Member createdMember = memberRepository.save(MemberConverter.toMember(memberData, provider));
                    Flower defaultFlower = flowerRepository.findByEmotion(Emotion.DEFAULT);

                    // 새 테라리움 생성
                    terrariumRepository.save(TerrariumConverter.toTerrarium(createdMember, defaultFlower));

                    return createdMember;
                });
    }
}
