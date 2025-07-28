package umc.plantory.domain.member.service;

import umc.plantory.domain.member.dto.MemberDataDTO;
import umc.plantory.domain.member.entity.Member;

public interface MemberCommandUseCase {
    Member findOrCreateMember(MemberDataDTO.KakaoMemberData kakaoMemberData);
}
