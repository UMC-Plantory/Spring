package umc.plantory.domain.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import umc.plantory.domain.member.client.KakaoOAuthClient;
import umc.plantory.domain.member.dto.*;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.mapping.MemberTerm;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.member.repository.MemberTermRepository;
import umc.plantory.domain.term.entity.Term;
import umc.plantory.domain.term.repository.TermRepository;
import umc.plantory.global.enums.Gender;
import umc.plantory.global.enums.MemberRole;
import umc.plantory.global.enums.MemberStatus;
import umc.plantory.global.enums.Provider;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberAuthServiceImpl implements MemberAuthService {
    private final KakaoOAuthClient kakaoOAuthClient;
    private final MemberRepository memberRepository;
    private final TermRepository termRepository;
    private final MemberTermRepository memberTermRepository;
    // 임시 닉네임
    private static final String TEMP_NICKNAME_PREFIX = "새싹이";
    private static final String DEFAULT_PROFILE_IMAGE_URL = "/images/default_profile.png"; // 나중에 S3 수정
    // "https://your-s3-bucket-name.s3.ap-northeast-2.amazonaws.com/profile/plantory_default_img.png";

    @Override
    public AuthResponse kakaoLogin(String code) {
        KakaoTokenResponse token = kakaoOAuthClient.requestToken(code);
        KakaoUserInfo userInfo = kakaoOAuthClient.requestUserInfo(token.getAccessToken());

        Optional<Member> existing = memberRepository.findByProviderAndProviderId(Provider.KAKAO, String.valueOf(userInfo.getId()));
        Member member;
        if (existing.isPresent()) {
            member = existing.get();
            return new AuthResponse(member.getId(), token.getAccessToken(), token.getRefreshToken(), false);
        }

        // 신규 유저 저장
        Member newMember = Member.builder()
                .email(userInfo.getKakaoAccount().getEmail())
                .nickname(TEMP_NICKNAME_PREFIX + System.currentTimeMillis())
                .profileImgUrl(DEFAULT_PROFILE_IMAGE_URL)
                .provider(Provider.KAKAO)
                .providerId(String.valueOf(userInfo.getId()))
                .role(MemberRole.USER)
                .status(MemberStatus.ACTIVE)
                .userId("kakao_" + userInfo.getId())
                .build();

        Member saved = memberRepository.save(newMember);
        return new AuthResponse(saved.getId(), token.getAccessToken(), token.getRefreshToken(), true);
    }

    @Transactional
    @Override
    public void saveMemberAgreements(Long memberId, AgreementRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 정보 없음"));

        Map<String, Boolean> agreementMap = request.getTermAgreement();
        if (agreementMap == null || agreementMap.isEmpty()) return;


        // 각 약관 동의 정보 처리
        for (Map.Entry<String, Boolean> entry : agreementMap.entrySet()) {
            String termSort = entry.getKey();
            Boolean isAgree = entry.getValue();

            // Term 엔티티 조회
            Term term = termRepository.findByTermSort(termSort)
                    .orElseThrow(() -> new RuntimeException("약관을 찾을 수 없습니다: " + termSort));

            // 해당 회원과 약관에 대한 MemberTerm이 이미 존재하는지 확인
            memberTermRepository.findByMemberAndTerm(member, term)
                    .ifPresentOrElse(
                            // 이미 존재하면 동의 여부 업데이트
                            memberTerm -> memberTerm.setIsAgree(isAgree),
                            // 존재하지 않으면 새로운 MemberTerm 생성 및 저장
                            () -> {
                                MemberTerm newMemberTerm = MemberTerm.builder()
                                        .member(member)
                                        .term(term)
                                        .isAgree(isAgree)
                                        .build();
                                memberTermRepository.save(newMemberTerm);
                            }
                    );
        }
    }

    @Transactional
    @Override
    public void setInfoForNewMember(Long memberId, AdditionalInfoRequest info) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 정보 없음"));

        member.setNickname(info.getNickname());
        member.setUserId(info.getUserId());
        member.setGender(Gender.valueOf(info.getGender()));
        member.setBirth(info.getBirth());
        memberRepository.save(member);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    @Override
    public boolean isNicknameExists(String nickname) {
        return memberRepository.findByNickname(nickname).isPresent();
    }
}
