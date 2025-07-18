package umc.plantory.domain.member.service;

import umc.plantory.domain.member.dto.AdditionalInfoRequest;
import umc.plantory.domain.member.dto.AgreementRequest;
import umc.plantory.domain.member.dto.AuthResponse;
import umc.plantory.domain.member.entity.Member;

import java.util.Optional;

public interface MemberAuthService {
    AuthResponse kakaoLogin(String code);
    void setInfoForNewMember(Long memberId, AdditionalInfoRequest infoRequest);
    Optional<Member> findById(Long memberId);
    void saveMemberAgreements(Long memberId, AgreementRequest request);
    boolean isNicknameExists(String nickname);
}