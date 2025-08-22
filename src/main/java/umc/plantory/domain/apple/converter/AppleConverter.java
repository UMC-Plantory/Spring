package umc.plantory.domain.apple.converter;

import io.jsonwebtoken.Claims;
import umc.plantory.domain.member.dto.MemberDataDTO;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.entity.Member;

import java.time.LocalDateTime;

public class AppleConverter {

    public static MemberDataDTO.MemberData toAppleMemberData(Claims claims) {
        return MemberDataDTO.MemberData.builder()
                .sub(claims.getSubject())
                .email(claims.get("email", String.class) != null ? claims.get("email", String.class) : "NONE")
                .build();
    }

    public static MemberResponseDTO.AppleOauth2LoginResponse toAppleOauth2LoginResponse(Member member, String accessToken, String refreshToken, LocalDateTime accessTokenExpireAt) {
        return MemberResponseDTO.AppleOauth2LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpireAt(accessTokenExpireAt)
                .memberStatus(member.getStatus())
                .build();
    }
}
