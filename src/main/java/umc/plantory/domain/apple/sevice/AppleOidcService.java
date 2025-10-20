package umc.plantory.domain.apple.sevice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import umc.plantory.domain.apple.converter.AppleConverter;
import umc.plantory.domain.member.dto.MemberDataDTO;
import umc.plantory.domain.member.dto.MemberRequestDTO;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.AppleHandler;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.net.URL;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleOidcService {

    // Apple JWKS
    private static final String JWK_URL = "https://appleid.apple.com/auth/keys";
    // iss 검증 값
    private static final String ISSUER = "https://appleid.apple.com";

    private final WebClient webClient;

    @Value("${apple.bundle-id}")
    private String BUNDLE_ID;

    /**
     * identity_token을 검증하고 담겨있는 멤버 데이터 추출
     */
    public MemberDataDTO.MemberData verifyAndParseIdToken(MemberRequestDTO.AppleOAuth2LoginRequest request) {
        try {
            String identityToken = request.getIdentityToken();
            String[] parts = identityToken.split("\\.");
            if (parts.length != 3) throw new AppleHandler(ErrorStatus.INVALID_JWT_TOKEN);

            // 헤더에서 kid 추출
            String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode header = mapper.readTree(headerJson);
            String kid = header.get("kid").asText();

            // JWKS 불러오기
            JsonNode keys = mapper.readTree(new URL(JWK_URL)).get("keys");
            JsonNode matchedKey = null;
            for (JsonNode key : keys) {
                if (key.get("kid").asText().equals(kid)) {
                    matchedKey = key;
                    break;
                }
            }
            if (matchedKey == null) throw new AppleHandler(ErrorStatus.ERROR_ON_VERIFYING);

            // 공개키 생성 (n, e)
            String n = matchedKey.get("n").asText();
            String e = matchedKey.get("e").asText();
            BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(n));
            BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(e));
            RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
            RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);

            // JJWT로 검증 (서명 + iss + exp)
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .requireIssuer(ISSUER)
                    .setAllowedClockSkewSeconds(300) // 시계오차 허용(5분)
                    .build();

            Claims claims = parser.parseClaimsJws(identityToken).getBody();

            // aud 검증: String 또는 List로 올 수 있음
            Object audObj = claims.get("aud");
            boolean audOk = false;
            if (audObj instanceof String audStr) {
                audOk = BUNDLE_ID.equals(audStr);
            } else if (audObj instanceof List<?> audList) {
                audOk = audList.stream().anyMatch(a -> BUNDLE_ID.equals(String.valueOf(a)));
            }
            if (!audOk) throw new AppleHandler(ErrorStatus.INVALID_JWT_TOKEN);

            return AppleConverter.toAppleMemberData(claims);

        } catch (ExpiredJwtException e) {
            throw new AppleHandler(ErrorStatus.EXPIRED_JWT_TOKEN);
        } catch (Exception e) {
            throw new AppleHandler(ErrorStatus.ERROR_ON_VERIFYING);
        }
    }

    /**
     * Authorization Code 를 통해 apple refresh_token 값을 받아오는 메서드
     */
    public String createAppleRefreshToken(String authorizationCode, String clientSecretJwt) {
        return webClient.post()
                .uri("https://appleid.apple.com/auth/token")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("code", authorizationCode)
                        .with("client_id", BUNDLE_ID)
                        .with("client_secret", clientSecretJwt))
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(System.out::println)
                .block();
    }
}
