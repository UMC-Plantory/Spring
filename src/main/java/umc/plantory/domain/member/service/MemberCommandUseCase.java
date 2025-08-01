package umc.plantory.domain.member.service;

import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.member.dto.MemberDataDTO;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.dto.MemberRequestDTO;
import umc.plantory.domain.member.dto.MemberResponseDTO;

public interface MemberCommandUseCase {
    MemberResponseDTO.TermAgreementResponse termAgreement(MemberRequestDTO.TermAgreementRequest request);
    MemberResponseDTO.MemberSignupResponse memberSignup(MemberRequestDTO.MemberSignupRequest request);
    void logout(String authorization);
    void delete(String authorization);
    Member findOrCreateMember(MemberDataDTO.KakaoMemberData kakaoMemberData);
}