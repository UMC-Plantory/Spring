package umc.plantory.domain.kakao.service;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import umc.plantory.domain.kakao.converter.KakaoConverter;
import umc.plantory.domain.member.dto.MemberDataDTO;
import umc.plantory.domain.member.dto.MemberRequestDTO;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.KakaoHandler;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;


@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoOidcService {
    // Kakao 공개키 -> Kakao 가 서명한 JWT(id_token) 을 검증하기 위해선 공개키(JWK) 필요
    private static final String JWK_URL = "https://kauth.kakao.com/.well-known/jwks.json";
    // ISSER 상수 -> id_token 안에 들어 있는 iss claim과 비교해 정품 Kakao 토큰인지 검증
    private static final String ISSUER = "https://kauth.kakao.com";

    /**
     * id_token을 검증하고 담겨있는 멤버 데이터 추출하는 메서드
     */
    public MemberDataDTO.KakaoMemberData verifyAndParseIdToken(MemberRequestDTO.KkoOAuth2LoginRequest request) {
        try {
            // id_token 디코딩 : 헤더와 payload 읽기 위해 필요
            DecodedJWT decodedJWT = JWT.decode(request.getIdToken());

            /*
            Kakao 공개키 가져오기
            decodedJWT.getKeyId() : JWT Header에 있는 kid 값
            이 kid에 해당하는 공개키를 Kakao의 JWK 서버에서 찾아옴
            반환된 Jwk 는 Kakao가 서명할 때 썼던 RSA 공개키를 포함
            */
            JwkProvider jwkProvider = new UrlJwkProvider(new URL((JWK_URL)));
            Jwk jwk = jwkProvider.get(decodedJWT.getKeyId());

            // 가져온 공개키로 RSA256 서명 검증용 알고리즘 생성 -> JWT는 서명 검증에만 공개키 사용 (비밀키 X -> null)
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

            // 알고리즘 + issuer 설정
            JWTVerifier jwtVerifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();

            // 실제 JWT 검증 수행
            DecodedJWT verified = jwtVerifier.verify(request.getIdToken());

            return KakaoConverter.toKakaoMemberData(verified);
        } catch (Exception e) {
            log.error("KakaoOidcService Error Occurred: {}", e.getMessage());
            throw new KakaoHandler(ErrorStatus.ERROR_ON_VERIFYING);
        }
    }
}
