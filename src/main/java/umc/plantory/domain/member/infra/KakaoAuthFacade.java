package umc.plantory.domain.member.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.plantory.domain.member.dto.KakaoTokenResponse;
import umc.plantory.domain.member.dto.KakaoUserInfo;
import umc.plantory.global.security.jwt.JwtProvider;
import umc.plantory.global.security.jwt.JwtResponseDTO;

// 카카오 인증/토큰/JWT 발급 등 외부 연동을 한 곳에서 퍼사드 패턴으로 묶어줌
@Service
@RequiredArgsConstructor
public class KakaoAuthFacade {
    private final KakaoOAuthClient kakaoOAuthClient; // 카카오 API 연동 퍼사드에 포함
    private final JwtProvider jwtProvider; // JWT 발급도 퍼사드에 포함

    // 카카오 토큰 발급 퍼사드로 감싸줌
    public KakaoTokenResponse requestToken(String code) {
        return kakaoOAuthClient.requestToken(code);
    }

    // 카카오 사용자 정보 조회 퍼사드로 감싸줌
    public KakaoUserInfo requestUserInfo(String accessToken) {
        return kakaoOAuthClient.requestUserInfo(accessToken);
    }

    // JWT 발급 퍼사드로 감싸줌
    public JwtResponseDTO.Login issueJwt(Long userId) {
        String accessToken = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);
        return JwtResponseDTO.Login.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
} 