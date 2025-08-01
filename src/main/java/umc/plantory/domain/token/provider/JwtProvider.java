package umc.plantory.domain.token.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.JwtHandler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access-token-expiration}")
    private Long ACCESS_TOKEN_EXPIRATION;
    @Value("${jwt.refresh-token-expiration}")
    private Long REFRESH_TOKEN_EXPIRATION;

    private final MemberRepository memberRepository;

    /**
     * Access Token 생성 메서드
     */
    public String generateAccessToken(Member member) {
        return JWT.create()
                .withSubject(member.getId().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .sign(Algorithm.HMAC256(secret)); // 비대칭이 아닌 대칭키 사용
    }

    /**
     * Refresh Token 생성
     */
    public String generateRefreshToken(Member member) {
        return JWT.create()
                .withSubject(member.getId().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * JWT 유효성 검증 (true/false)
     */
    public boolean validateToken(String token) {
        try {
            JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
            return true;
        } catch (TokenExpiredException e) {
            throw new JwtHandler(ErrorStatus.EXPIRED_JWT_TOKEN);
        } catch (Exception e) {
            throw new JwtHandler(ErrorStatus.INVALID_JWT_TOKEN);
        }
    }

    /**
     * JWT에서 사용자 ID 추출
     */
    public Long getMemberId(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return Long.valueOf(decodedJWT.getSubject());
        } catch (Exception e) {
            throw new JwtHandler(ErrorStatus.INVALID_JWT_TOKEN);
        }
    }

    /**
     * Authorization 헤더에서 실제 토큰 추출
     */
    public String resolveToken(String bearerHeader) {
        if (bearerHeader != null && bearerHeader.startsWith("Bearer ")) {
            return bearerHeader.substring(7);
        }
        return null;
    }

    /**
     * 만료 시간 추출
     */
    public LocalDateTime getExpiredAt(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(token);
            return LocalDateTime.ofInstant(decodedJWT.getExpiresAt().toInstant(), ZoneId.systemDefault());
        } catch (Exception e) {
            throw new JwtHandler(ErrorStatus.INVALID_JWT_TOKEN);
        }
    }
}