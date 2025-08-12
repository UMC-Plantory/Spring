package umc.plantory.domain.member.service;

import umc.plantory.domain.member.dto.MemberDataDTO;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.dto.MemberRequestDTO;
import umc.plantory.domain.member.dto.MemberResponseDTO;

public interface MemberCommandUseCase {
    MemberResponseDTO.TermAgreementResponse termAgreement(String authorization, MemberRequestDTO.TermAgreementRequest request);
    MemberResponseDTO.MemberSignupResponse memberSignup(String authorization, MemberRequestDTO.MemberSignupRequest request);
    MemberResponseDTO.ProfileUpdateResponse updateProfile(String authorization, MemberRequestDTO.ProfileUpdateRequest request);
    void logout(String authorization);
    void delete(String authorization);
    Member findOrCreateMember(MemberDataDTO.KakaoMemberData kakaoMemberData);
}