package umc.plantory.domain.member.service;

import umc.plantory.domain.member.dto.MemberRequestDTO;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.global.security.jwt.JwtResponseDTO;
import umc.plantory.domain.member.entity.Member;

import java.util.Map;
import java.util.Optional;

public interface MemberAuthService {
    MemberResponseDTO.MemberAuth kakaoLogin(String code);
    MemberResponseDTO.MemberProfile setInfoForNewMember(Long memberId, MemberRequestDTO.AdditionalInfo infoRequest);
    Optional<Member> findById(Long memberId);
    Map<String, Object> saveMemberAgreements(Long memberId, MemberRequestDTO.Agreement request);
    boolean isNicknameExists(String nickname);
    boolean validateRequiredTerms(MemberRequestDTO.Agreement request);
    boolean validateAdditionalInfo(MemberRequestDTO.AdditionalInfo request);
    JwtResponseDTO.Login kakaoLoginAndIssueJwt(String code);
    Long extractMemberIdFromAuthorization(String authorizationHeader);
}