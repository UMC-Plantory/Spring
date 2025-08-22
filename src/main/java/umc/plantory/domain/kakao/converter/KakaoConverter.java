package umc.plantory.domain.kakao.converter;

import io.jsonwebtoken.Claims;
import umc.plantory.domain.member.dto.MemberDataDTO;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.entity.Member;

import java.time.LocalDateTime;

public class KakaoConverter {

    public static MemberDataDTO.MemberData toKakaoMemberData(Claims claims) {
        return MemberDataDTO.MemberData.builder()
                .sub(claims.getSubject())
                .email(claims.get("email", String.class))
                .build();
    }

    public static MemberResponseDTO.KkoOAuth2LoginResponse toKkoOAuth2LoginResponse(Member member, String accessToken, String refreshToken, LocalDateTime accessTokenExpireAt) {
        return MemberResponseDTO.KkoOAuth2LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpireAt(accessTokenExpireAt)
                .memberStatus(member.getStatus())
                .build();
    }
}
