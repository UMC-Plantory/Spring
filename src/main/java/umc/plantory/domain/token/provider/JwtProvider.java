package umc.plantory.domain.token.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.JwtHandler;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
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

    private Key key;

    // PostConstruct 사용으로 미리 키를 한 번 생성하면 매번 Alogorithm 만들지 않아도 됨.
    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getEncoder().encode(secret.getBytes());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Access Token 생성 메서드
     */
    public String generateAccessToken(Member member) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION);

        return Jwts.builder()
                .setSubject(member.getId().toString())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Refresh Token 생성
     */
    public String generateRefreshToken(Member member) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION);

        return Jwts.builder()
                .setSubject(member.getId().toString())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 유효성 검증 (true/false)
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new JwtHandler(ErrorStatus.EXPIRED_JWT_TOKEN);
        } catch (Exception e) {
            throw new JwtHandler(ErrorStatus.INVALID_JWT_TOKEN);
        }
    }

    /**
     * Bearer 삭제 후 검증하고 memberId 추출하는 메서드
     */
    public Long getMemberIdAndValidateToken(String bearerToken) {
        try {
            // Bearer 삭제
            String token = resolveToken(bearerToken);
            if (token == null) throw new JwtHandler(ErrorStatus._UNAUTHORIZED);

            // 유효성 검증
            validateToken(token);

            return getMemberId(token);
        } catch (Exception e) {
            throw new JwtHandler(ErrorStatus.INVALID_JWT_TOKEN);
        }
    }

    /**
     * JWT에서 사용자 ID 추출
     */
    public Long getMemberId(String token) {
        try {
            // 추출
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token).getBody();
            return Long.valueOf(claims.getSubject());
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
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token).getBody();
            Date expiration = claims.getExpiration();
            return LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault());
        } catch (Exception e) {
            throw new JwtHandler(ErrorStatus.INVALID_JWT_TOKEN);
        }
    }
}
