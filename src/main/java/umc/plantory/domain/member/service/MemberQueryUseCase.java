package umc.plantory.domain.member.service;

import umc.plantory.domain.member.dto.MemberResponseDTO;

import java.time.YearMonth;

public interface MemberQueryUseCase {
    MemberResponseDTO.ProfileResponse getProfile(String authorization);
    MemberResponseDTO.HomeResponse getHome(String authorization, YearMonth yearMonth);
}