package umc.plantory.domain.kakao.converter;

import io.jsonwebtoken.Claims;
import umc.plantory.domain.member.dto.MemberDataDTO;
import umc.plantory.domain.member.dto.MemberResponseDTO;

import java.time.LocalDateTime;

public class KakaoConverter {

    public static MemberDataDTO.KakaoMemberData toKakaoMemberData(Claims claims) {
        return MemberDataDTO.KakaoMemberData.builder()
                .sub(claims.getSubject())
                .email(claims.get("email", String.class))
                .build();
    }

    public static MemberResponseDTO.KkoOAuth2LoginResponse toKkoOAuth2LoginResponse(String accessToken, String refreshToken, LocalDateTime accessTokenExpireAt) {
        return MemberResponseDTO.KkoOAuth2LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpireAt(accessTokenExpireAt)
                .build();
    }
}
