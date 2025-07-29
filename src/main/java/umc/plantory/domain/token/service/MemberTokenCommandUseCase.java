package umc.plantory.domain.token.service;

import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.entity.Member;

public interface MemberTokenCommandUseCase {
    MemberResponseDTO.KkoOAuth2LoginResponse generateToken(Member member);
}
