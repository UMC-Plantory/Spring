package umc.plantory.domain.kakao.converter;

import com.auth0.jwt.interfaces.DecodedJWT;
import umc.plantory.domain.member.dto.MemberDataDTO;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.global.enums.Gender;

import java.time.LocalDateTime;

public class KakaoConverter {

    public static MemberDataDTO.KakaoMemberData toKakaoMemberData(DecodedJWT verified) {
        return MemberDataDTO.KakaoMemberData.builder()
                .sub(verified.getSubject())
                .email(verified.getClaim("email").asString())
                .build();
    }

    public static MemberResponseDTO.KkoOAuth2LoginResponse toKkoOAuth2LoginResponse(String accessToken, String refreshToken, LocalDateTime accessTokenExpiredAt) {
        return MemberResponseDTO.KkoOAuth2LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiredAt(accessTokenExpiredAt)
                .build();
    }
}
