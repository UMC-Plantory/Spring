package umc.plantory.domain.member.service;

import umc.plantory.domain.member.dto.MemberResponseDTO;

public interface MemberQueryUseCase {
    MemberResponseDTO.ProfileResponse getProfile(Long memberId);
} 