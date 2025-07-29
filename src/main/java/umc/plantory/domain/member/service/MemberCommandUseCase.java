package umc.plantory.domain.member.service;

import umc.plantory.domain.member.dto.MemberRequestDTO;
import umc.plantory.domain.member.dto.MemberResponseDTO;

public interface MemberCommandUseCase {
    MemberResponseDTO.TermAgreementResponse termAgreement(MemberRequestDTO.TermAgreementRequest request);
    MemberResponseDTO.MemberSignupResponse memberSignup(MemberRequestDTO.MemberSignupRequest request);
}
