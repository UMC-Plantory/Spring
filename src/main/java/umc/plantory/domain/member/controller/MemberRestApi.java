package umc.plantory.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import umc.plantory.global.apiPayload.ApiResponse;
import umc.plantory.domain.member.dto.*;
import umc.plantory.global.security.jwt.JwtResponseDTO;
import java.util.Map;

public interface MemberRestApi {
    @Tag(name = "카카오 로그인")
    @Operation(summary = "카카오 인가 URL 반환", description = "카카오 OAuth2 인가를 위한 URL을 반환합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "인가 URL 반환 성공")
    })
    ApiResponse<String> getKakaoAuthorizeUrl();

    @Tag(name = "카카오 로그인")
    @Operation(summary = "카카오 로그인 처리", description = "카카오 인가 코드를 받아 로그인 및 JWT 토큰을 발급합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 및 토큰 발급 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    ApiResponse<JwtResponseDTO.Login> kakaoLogin(@RequestParam("code") String code);

    @Tag(name = "카카오 로그인")
    @Operation(summary = "회원가입 약관 동의", description = "회원가입 시 필수 및 선택 약관 동의 정보를 저장합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "약관 동의 저장 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "필수 약관 미동의 또는 잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저 정보 없음")
    })
    ApiResponse<Map<String, Object>> saveAgreements(
            @Parameter(description = "Bearer 토큰", required = true)
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
            @RequestBody MemberRequestDTO.Agreement agreementRequest);

    @Tag(name = "카카오 로그인")
    @Operation(summary = "회원가입 추가 정보 입력", description = "신규 회원의 추가 정보를 입력받아 저장합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "추가 정보 저장 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "필수 정보 누락 또는 잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저 정보 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "중복 닉네임")
    })
    ApiResponse<MemberResponseDTO.MemberProfile> setInfo(
            @Parameter(description = "Bearer 토큰", required = true)
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
            @RequestBody MemberRequestDTO.AdditionalInfo infoRequest);
}
