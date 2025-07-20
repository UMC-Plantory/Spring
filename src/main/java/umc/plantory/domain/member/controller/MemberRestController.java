package umc.plantory.domain.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import umc.plantory.domain.member.dto.MemberRequestDTO;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.service.MemberAuthService;
import umc.plantory.global.apiPayload.ApiResponse;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.MemberHandler;
import umc.plantory.global.exception.KakaoApiException;
import umc.plantory.global.security.jwt.JwtResponseDTO;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/plantory/member")
@Slf4j
public class MemberRestController implements MemberRestApi {
    private final MemberAuthService memberAuthService;

    @Value("${kakao.authorization-uri}")
    private String kakaoAuthorizeUri;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    /**
     * 카카오 OAuth2 인가 URL을 반환합니다.
     *
     * @return 인가 URL이 담긴 ApiResponse
     */
    @Override
    @GetMapping("/kakao/authorize")
    public ApiResponse<String> getKakaoAuthorizeUrl() {
        String url = kakaoAuthorizeUri +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code";
        return ApiResponse.onSuccess(url);
    }

    /**
     * 카카오 로그인 처리 및 JWT 토큰을 발급합니다.
     *
     * @param code 카카오 인가 코드
     * @return JWT 엑세스/리프레시 토큰이 담긴 ApiResponse
     * @throws KakaoApiException 카카오 API 호출 실패 시
     * @throws MemberHandler     서버 내부 오류 시
     */
    @Override
    @GetMapping("/login")
    public ApiResponse<JwtResponseDTO.Login> kakaoLogin(String code) {
        try {
            JwtResponseDTO.Login jwtLoginDto = memberAuthService.kakaoLoginAndIssueJwt(code);
            return ApiResponse.onSuccess(jwtLoginDto);
        } catch (KakaoApiException e) {
            throw e;
        } catch (Exception e) {
            throw new MemberHandler(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 회원가입 시 필수 및 선택 약관 동의 정보를 저장합니다.
     *
     * @param authorizationHeader Bearer 토큰 헤더
     * @param agreementRequest    약관 동의 요청 DTO
     * @return 약관 동의 결과 데이터가 담긴 ApiResponse
     * @throws MemberHandler 검증 실패 시
     */
    @Override
    @PostMapping("/agreements")
    public ApiResponse<Map<String, Object>> saveAgreements(String authorizationHeader, MemberRequestDTO.Agreement agreementRequest) {
        if (!memberAuthService.validateRequiredTerms(agreementRequest)) {
            throw new MemberHandler(ErrorStatus._BAD_REQUEST);
        }
        Map<String, Object> data = memberAuthService.saveMemberAgreements(
                memberAuthService.extractMemberIdFromAuthorization(authorizationHeader)
                , agreementRequest);
        return ApiResponse.onSuccess(data);
    }

    /**
     * 신규 회원의 추가 정보를 입력받아 저장합니다.
     *
     * @param authorizationHeader Bearer 토큰 헤더
     * @param infoRequest         추가 정보 요청 DTO
     * @return 회원 프로필 데이터가 담긴 ApiResponse
     * @throws MemberHandler 검증 실패 또는 닉네임 중복 시
     */
    @Override
    @PostMapping("/first")
    public ApiResponse<MemberResponseDTO.MemberProfile> setInfo(String authorizationHeader, MemberRequestDTO.AdditionalInfo infoRequest) {
        if (!memberAuthService.validateAdditionalInfo(infoRequest)) {
            throw new MemberHandler(ErrorStatus._BAD_REQUEST);
        }
        if (memberAuthService.isNicknameExists(infoRequest.getNickname())) {
            throw new MemberHandler(ErrorStatus._CONFLICT);
        }
        MemberResponseDTO.MemberProfile memberProfile = memberAuthService.setInfoForNewMember(
                memberAuthService.extractMemberIdFromAuthorization(authorizationHeader)
                , infoRequest);
        return ApiResponse.onSuccess(memberProfile);
    }
}


