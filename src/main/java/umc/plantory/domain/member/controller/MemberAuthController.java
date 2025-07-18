package umc.plantory.domain.member.controller;


import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.plantory.domain.member.dto.AdditionalInfoRequest;
import umc.plantory.domain.member.dto.AgreementRequest;
import umc.plantory.domain.member.dto.AuthResponse;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.service.MemberAuthService;
//import umc.plantory.global.apiPayload.ApiResponse;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.exception.KakaoApiException;
import umc.plantory.global.security.jwt.JwtProvider;
import umc.plantory.global.security.jwt.JwtResDTO;
import umc.plantory.domain.term.repository.TermRepository;
import umc.plantory.domain.term.entity.Term;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/plantory/auth")
@Slf4j
public class MemberAuthController {
    private final MemberAuthService memberAuthService;
    private final JwtProvider jwtProvider; // JwtProvider 주입
    private final TermRepository termRepository;

    @Value("${kakao.authorization-uri}")
    private String kakaoAuthorizeUri;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    // 1. 인가 url 생성
    @Operation(summary = "카카오 인가 URL 반환", description = "카카오 OAuth2 인가를 위한 URL을 반환합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "인가 URL 반환 성공")
    })
    @GetMapping("/kakao/authorize")
    public ResponseEntity<String> getKakaoAuthorizeUrl() {
        String url = kakaoAuthorizeUri +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code";
        return ResponseEntity.ok(url); // 1번만 리다이렉트 URL 반환
    }

    // 2. 카카오 인가 코드로 로그인 처리
    @Operation(summary = "카카오 로그인 처리", description = "카카오 인가 코드를 받아 로그인 및 JWT 토큰을 발급합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 및 토큰 발급 성공"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/kakao/login")
    public ResponseEntity<?> kakaoLogin(@RequestParam("code") String code) {
        try {
            // 카카오 인가코드를 이용해 회원 인증 및 정보 조회
            AuthResponse auth = memberAuthService.kakaoLogin(code);
            Long userId = auth.getUserId();

            // JWT 엑세스/리프레시 토큰 발급
            String accessToken = jwtProvider.createAccessToken(userId);
            String refreshToken = jwtProvider.createRefreshToken(userId);
            JwtResDTO.Login jwtLoginDto = new JwtResDTO.Login(accessToken, refreshToken);
            return ResponseEntity.ok(umc.plantory.global.apiPayload.ApiResponse.onSuccess(jwtLoginDto));
        } catch (KakaoApiException e) {
            // 카카오 API 관련 예외는 글로벌 핸들러로 위임
            throw e;
        } catch (Exception e) {
            // 그 외 서버 내부 예외 처리
            return ResponseEntity.status(ErrorStatus._INTERNAL_SERVER_ERROR.getHttpStatus())
                    .body(umc.plantory.global.apiPayload.ApiResponse.onFailure(
                            ErrorStatus._INTERNAL_SERVER_ERROR.getCode(),
                            ErrorStatus._INTERNAL_SERVER_ERROR.getMessage(),
                            null));
        }
    }

    // 3. 회원가입 약관 동의
    @Operation(summary = "회원가입 약관 동의", description = "회원가입 시 필수 및 선택 약관 동의 정보를 저장합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "약관 동의 저장 성공"),
        @ApiResponse(responseCode = "400", description = "필수 약관 미동의 또는 잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "유저 정보 없음")
    })
    @PostMapping("/member/agreements")
    public ResponseEntity<?> saveAgreements(
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
            @RequestBody AgreementRequest agreementRequest) {
        // 인증 헤더 검사
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(ErrorStatus._UNAUTHORIZED.getHttpStatus())
                .body(umc.plantory.global.apiPayload.ApiResponse.onFailure(
                    ErrorStatus._UNAUTHORIZED.getCode(),
                    ErrorStatus._UNAUTHORIZED.getMessage(),
                    null));
        }
        // JWT에서 사용자 id 추출
        String accessToken = authorizationHeader.substring(7);
        Long memberId = jwtProvider.getUserIdFromToken(accessToken);

        // 약관 동의 정보 받아오기 (필수 약관 동의 여부 체크)
        Map<String, Boolean> agreementMap = agreementRequest.getTermAgreement();
        if (agreementMap == null) agreementMap = Map.of();
        var requiredTerms = termRepository.findByIsRequiredTrue(); // db에 over14가 있나?
        for (Term term : requiredTerms) {
            // 필수 약관을 모두 동의하지 않은 경우 에러 리턴
            if (!agreementMap.getOrDefault(term.getTermSort(), false)) {
                return ResponseEntity.status(ErrorStatus._BAD_REQUEST.getHttpStatus())
                    .body(umc.plantory.global.apiPayload.ApiResponse.onFailure(
                        ErrorStatus._BAD_REQUEST.getCode(),
                        "필수 약관에 모두 동의해야 합니다.",
                        null));
            }
        }
        memberAuthService.saveMemberAgreements(memberId, agreementRequest);
        Member member = memberAuthService.findById(memberId).orElse(null);
        if (member == null) {
            return ResponseEntity.status(ErrorStatus._NOT_FOUND.getHttpStatus())
                .body(umc.plantory.global.apiPayload.ApiResponse.onFailure(
                    ErrorStatus._NOT_FOUND.getCode(),
                    "유저 정보를 찾을 수 없습니다.",
                    null));
        }
        Map<String, Object> data = Map.of(
            "email", member.getEmail()
        );
        return ResponseEntity.ok(umc.plantory.global.apiPayload.ApiResponse.onSuccess(data));
    }

    // 4. 회원가입 정보 추가 입력 (신규 유저)
    @Operation(summary = "회원가입 추가 정보 입력", description = "신규 회원의 추가 정보를 입력받아 저장합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "추가 정보 저장 성공"),
        @ApiResponse(responseCode = "400", description = "필수 정보 누락 또는 잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "유저 정보 없음"),
        @ApiResponse(responseCode = "409", description = "중복 닉네임")
    })
    @PostMapping("/member/additional")
    public ResponseEntity<?> setInfo(
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
            @RequestBody AdditionalInfoRequest infoRequest) {
        // 인증 헤더 검사
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(ErrorStatus._UNAUTHORIZED.getHttpStatus())
                    .body(umc.plantory.global.apiPayload.ApiResponse.onFailure(
                            ErrorStatus._UNAUTHORIZED.getCode(),
                            ErrorStatus._UNAUTHORIZED.getMessage(),
                            null));
        }
        // JWT에서 사용자 id 추출
        String accessToken = authorizationHeader.substring(7);
        Long memberId = jwtProvider.getUserIdFromToken(accessToken);

        // 필수 추가 정보 모두 입력되었는지 체크
        if (infoRequest.getNickname() == null || infoRequest.getUserId() == null
                || infoRequest.getGender() == null || infoRequest.getBirth() == null) {
            return ResponseEntity.status(ErrorStatus._BAD_REQUEST.getHttpStatus())
                    .body(umc.plantory.global.apiPayload.ApiResponse.onFailure(
                            ErrorStatus._BAD_REQUEST.getCode(),
                            "필수 정보가 누락되었습니다.",
                            null));
        }

        // 닉네임 중복 검사
        if (memberAuthService.isNicknameExists(infoRequest.getNickname())) {
            return ResponseEntity.status(ErrorStatus._CONFLICT.getHttpStatus())
                    .body(umc.plantory.global.apiPayload.ApiResponse.onFailure(
                            ErrorStatus._CONFLICT.getCode(),
                            "이미 사용 중인 이름입니다.",
                            null));
        }
        memberAuthService.setInfoForNewMember(memberId, infoRequest);
        Member member = memberAuthService.findById(memberId).orElse(null);
        if (member == null) {
            return ResponseEntity.status(ErrorStatus._NOT_FOUND.getHttpStatus())
                    .body(umc.plantory.global.apiPayload.ApiResponse.onFailure(
                            ErrorStatus._NOT_FOUND.getCode(),
                            "유저 정보를 찾을 수 없습니다.",
                            null));
        }
        Map<String, Object> data = Map.of(
                "memberId", member.getId(),
                "nickname", member.getNickname(),
                "email", member.getEmail(),
                "gender", member.getGender() != null ? member.getGender().toString().toLowerCase() : null,
                "birth", member.getBirth() != null ? member.getBirth().toString() : null,
                "profileImgUrl", member.getProfileImgUrl()
        );
        return ResponseEntity.ok(umc.plantory.global.apiPayload.ApiResponse.onSuccess(data));
    }
}


