package umc.plantory.domain.member.service;

import umc.plantory.domain.member.dto.MemberResponseDTO;

import java.time.LocalDate;

public interface MemberQueryUseCase {
    MemberResponseDTO.ProfileResponse getProfile(String authorization);
    MemberResponseDTO.HomeResponse getHome(String authorization, LocalDate selectedDate);
}