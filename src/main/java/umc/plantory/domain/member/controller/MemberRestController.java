package umc.plantory.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.plantory.domain.apple.sevice.AppleOidcService;
import umc.plantory.domain.kakao.service.KakaoOidcService;
import umc.plantory.domain.member.dto.MemberDataDTO;
import umc.plantory.domain.member.dto.MemberRequestDTO;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.service.MemberCommandUseCase;
import umc.plantory.domain.member.service.MemberQueryUseCase;
import umc.plantory.domain.token.service.MemberTokenCommandUseCase;
import umc.plantory.global.apiPayload.ApiResponse;
import umc.plantory.global.enums.Provider;

@Slf4j
@RestController
@RequestMapping("/v1/plantory/members")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 관련 API")
public class MemberRestController {
    private final KakaoOidcService kakaoOidcService;
    private final AppleOidcService appleOidcService;
    private final MemberCommandUseCase memberCommandUseCase;
    private final MemberQueryUseCase memberQueryUseCase;
    private final MemberTokenCommandUseCase memberTokenService;

    @GetMapping("/profile")
    @Operation(summary = "마이페이지 프로필 조회 API", description = "회원의 프로필 정보와 통계를 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<MemberResponseDTO.ProfileResponse>> getProfile(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return ResponseEntity.ok(ApiResponse.onSuccess(memberQueryUseCase.getProfile(authorization)));
    }

    @GetMapping("/myprofile")
    @Operation(summary = "마이프로필 조회 API", description = "회원의 상세 프로필 정보를 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<MemberResponseDTO.MyProfileResponse>> getMyProfile(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return ResponseEntity.ok(ApiResponse.onSuccess(memberQueryUseCase.getMyProfile(authorization)));
    }

    @PatchMapping("/profile")
    @Operation(summary = "프로필 수정 API", description = "회원의 프로필 정보(닉네임, 사용자 커스텀 ID, 성별, 생년월일, 프로필 이미지, 이메일)를 수정하는 API입니다.")
    public ResponseEntity<ApiResponse<MemberResponseDTO.ProfileUpdateResponse>> updateProfile(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody MemberRequestDTO.ProfileUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.onSuccess(memberCommandUseCase.updateProfile(authorization, request)));
    }

    @PostMapping("/agreements")
    @Operation(summary = "약관 동의 API", description = "회원이 약관에 동의하는 API입니다.")
    public ResponseEntity<ApiResponse<MemberResponseDTO.TermAgreementResponse>> termAgreement(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody MemberRequestDTO.TermAgreementRequest request) {
        return ResponseEntity.ok(ApiResponse.onSuccess(memberCommandUseCase.termAgreement(authorization, request)));
    }

    @PatchMapping("/signup")
    @Operation(summary = "회원가입 완료 API", description = "회원의 추가 정보(닉네임, 사용자 커스텀 ID, 성별, 생년월일, 프로필 이미지)를 입력하여 회원가입을 완료하는 API입니다.")
    public ResponseEntity<ApiResponse<MemberResponseDTO.MemberSignupResponse>> signup(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody MemberRequestDTO.MemberSignupRequest request) {
        return ResponseEntity.ok(ApiResponse.onSuccess(memberCommandUseCase.memberSignup(authorization, request)));
    }

    @PostMapping("/auth/kko")
    @Operation(summary = "KAKAO OAuth2 로그인 API", description = "KAKAO OAuth2 로그인 API 입니다.")
    public ResponseEntity<ApiResponse<MemberResponseDTO.KkoOAuth2LoginResponse>> kkoOAuth2Login (@Valid @RequestBody MemberRequestDTO.KkoOAuth2LoginRequest request) {
        // id_token 검증 후 멤버 데이터 추출
        MemberDataDTO.MemberData kakaoMemberData = kakaoOidcService.verifyAndParseIdToken(request);

        // id_token 에서 추출한 데이터를 통해 멤버 조회 OR 생성
        Member findOrCreateMember = memberCommandUseCase.findOrCreateMember(kakaoMemberData, Provider.KAKAO, request.getFcmToken());

        // 토큰 생성 및 응답
        return ResponseEntity.ok(ApiResponse.onSuccess(memberTokenService.generateKkoLoginToken(findOrCreateMember)));
    }

    @PatchMapping("/auth/apple")
    @Operation(summary = "APPLE Oauth2 로그인 API", description = "APPLE OAuth2 로그인 API 입니다.")
    public ResponseEntity<ApiResponse<MemberResponseDTO.AppleOauth2LoginResponse>> appleOauth2Login (@Valid @RequestBody MemberRequestDTO.AppleOAuth2LoginRequest request) {
        // identity_token 검증 후 멤버 데이터 추출
        MemberDataDTO.MemberData appleMemberData = appleOidcService.verifyAndParseIdToken(request);

        // identity_token 에서 추출한 데이터를 통해 멤버 조회 OR 생성
        Member findOrCreateMember = memberCommandUseCase.findOrCreateMember(appleMemberData, Provider.APPLE, request.getFcmToken());

        return ResponseEntity.ok(ApiResponse.onSuccess(memberTokenService.generateAppleLoginToken(findOrCreateMember)));
    }

    @DeleteMapping("/auth")
    @Operation(summary = "로그아웃 API", description = "로그아웃 API입니다.")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        memberCommandUseCase.logout(authorization);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @PatchMapping
    @Operation(summary = "계정 탈퇴 API", description = "계정 탈퇴 API입니다.")
    public ResponseEntity<ApiResponse<Void>> deleteMember(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        memberCommandUseCase.delete(authorization);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }


}
