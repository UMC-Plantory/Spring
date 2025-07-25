package umc.plantory.domain.member.service;

import umc.plantory.domain.member.dto.MemberRequestDTO;
import umc.plantory.domain.member.dto.MemberResponseDTO;

public interface MemberCommandUseCase {
    public MemberResponseDTO.MemberSignupResponse memberSignup(MemberRequestDTO.MemberSignupRequest request);
}
