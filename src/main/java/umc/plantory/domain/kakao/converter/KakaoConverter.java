package umc.plantory.domain.kakao.converter;

import com.auth0.jwt.interfaces.DecodedJWT;
import umc.plantory.domain.member.dto.MemberDataDTO;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.global.enums.Gender;

public class KakaoConverter {

    public static MemberDataDTO.KakaoMemberData toKakaoMemberData(DecodedJWT verified) {
        return MemberDataDTO.KakaoMemberData.builder()
                .sub(verified.getSubject())
                .nickname(verified.getClaim("nickname").asString())
                .email(verified.getClaim("email").asString())
                .gender(verified.getClaim("gender").asString().equals("male") ? Gender.MALE : Gender.FEMALE )
                .build();
    }

    public static MemberResponseDTO.KkoOAuth2LoginResponse toKkoOAuth2LoginResponse(String accessToken, String refreshToken) {
        return MemberResponseDTO.KkoOAuth2LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
