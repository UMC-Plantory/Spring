package umc.plantory.global.security.jwt;

import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties props;

    public String createAccessToken(Long userId) {
        return createToken(userId, props.getAccessTokenExpiration());
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, props.getRefreshTokenExpiration());
    }

    private String createToken(Long userId, long expiration) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, props.getSecret())
                .compact();
    }

    public void addTokenCookies(HttpServletResponse res, JwtResponseDTO.Login dto) {
        addCookie(res, "access_token",  dto.getAccessToken(),  props.getAccessTokenExpiration());
        addCookie(res, "refresh_token", dto.getRefreshToken(), props.getRefreshTokenExpiration());
    }

    private void addCookie(HttpServletResponse res, String name, String value, long maxAgeMs) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) (maxAgeMs / 1000));   // 초 단위
        res.addCookie(cookie);
    }

    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(props.getSecret())
                    .parseClaimsJws(token)
                    .getBody();
            return Long.parseLong(claims.getSubject());
        } catch (ExpiredJwtException e) {
            throw new CustomJwtException("만료된 토큰입니다.");
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomJwtException("유효하지 않은 토큰입니다.");
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(props.getSecret()).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
