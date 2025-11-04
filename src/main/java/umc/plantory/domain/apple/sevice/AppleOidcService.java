package umc.plantory.domain.apple.sevice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import umc.plantory.domain.apple.converter.AppleConverter;
import umc.plantory.domain.member.dto.MemberDataDTO;
import umc.plantory.domain.member.dto.MemberRequestDTO;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.AppleHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleOidcService {

    // Apple JWKS
    private static final String JWK_URL = "https://appleid.apple.com/auth/keys";
    // iss 검증 값
    private static final String ISSUER = "https://appleid.apple.com";
    // 약 30일
    private static final long MAX_EXP_SECONDS = 60L * 60L * 24L * 30L;

    private final WebClient webClient;

    @Value("${apple.bundle-id}")
    private String BUNDLE_ID;
    @Value("${apple.p8.location:}")
    private Resource p8Resource;
    @Value("${apple.key-id}")
    private String KEY_ID;
    @Value("${apple.team-id}")
    private String TEAM_ID;


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
    public String createAppleRefreshToken(String authorizationCode, String clientSecret) {
        return webClient.post()
                .uri("https://appleid.apple.com/auth/token")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("code", authorizationCode)
                        .with("client_id", BUNDLE_ID)
                        .with("client_secret", clientSecret))
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(System.out::println)
                .block();
    }

    /**
     * Apple Client_Secret 생성 메서드
     */
    public String createAppleClientSecret() {
        try {
            ECPrivateKey privateKey = (ECPrivateKey) loadPrivateKeyFromPem();

            Date iat = Date.from(Instant.now());
            Date exp = Date.from(Instant.now().plusSeconds(MAX_EXP_SECONDS));

            String jwt = Jwts.builder()
                    .setHeaderParam("kid", KEY_ID)
                    .setIssuer(TEAM_ID)
                    .setSubject(BUNDLE_ID)
                    .setAudience(ISSUER)
                    .setIssuedAt(iat)
                    .setExpiration(exp)
                    .signWith(privateKey, SignatureAlgorithm.ES256)
                    .compact();

            return jwt;
        } catch (Exception e) {
            log.error("Failed to Refresh Apple Client_Secret", e);
            throw new RuntimeException("Failed to Refresh Apple Client_Secret", e);
        }
    }

    /**
     * pem 파일에 적힌 privateKey 읽어오는 메서드
     */
    private PrivateKey loadPrivateKeyFromPem() throws Exception {
        String pem;
        if (p8Resource != null && p8Resource.exists()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p8Resource.getInputStream(), StandardCharsets.UTF_8))) {
                pem = br.lines().collect(Collectors.joining("\n"));
            }
        } else {
            throw new AppleHandler(ErrorStatus._INTERNAL_SERVER_ERROR);
        }

        String normalized = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] der = Base64.getDecoder().decode(normalized);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(der);
        KeyFactory kf = KeyFactory.getInstance("EC");
        return kf.generatePrivate(keySpec);
    }

    /**
     * 플랜토리 - 애플 연동 해제 메서드
     */
    public void unlinkUser(String refreshToken, String clientSecret) {
        webClient.post()
                .uri("https://appleid.apple.com/auth/revoke")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .body(BodyInserters.fromFormData("token_type_hint", "refresh_token")
                        .with("token", refreshToken)
                        .with("client_id", BUNDLE_ID)
                        .with("client_secret", clientSecret))
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(System.out::println)
                .block();
    }
}
