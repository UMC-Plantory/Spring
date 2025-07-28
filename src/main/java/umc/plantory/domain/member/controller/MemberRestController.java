package umc.plantory.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.plantory.domain.kakao.service.KakaoOidcService;
import umc.plantory.domain.member.dto.MemberDataDTO;
import umc.plantory.domain.member.dto.MemberRequestDTO;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.service.MemberCommandService;
import umc.plantory.domain.token.service.MemberTokenCommandService;
import umc.plantory.global.apiPayload.ApiResponse;

@RestController
@RequestMapping("/v1/plantory/member")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 관련 API")
public class MemberRestController {
    private final KakaoOidcService kakaoOidcService;
    private final MemberCommandService memberService;
    private final MemberTokenCommandService memberTokenService;

    @PostMapping("/kko/login")
    @Operation(summary = "KAKAO OAuth2 로그인 API", description = "KAKAO OAuth2 로그인 API 입니다.")
    public ResponseEntity<ApiResponse<MemberResponseDTO.KkoOAuth2LoginResponse>> kkoOAuth2Login
            (@RequestBody MemberRequestDTO.KkoOAuth2LoginRequest request) {
        // id_token 검증 후 멤버 데이터 추출
        MemberDataDTO.KakaoMemberData kakaoMemberData = kakaoOidcService.verifyAndParseIdToken(request);

        // id_token 에서 추출한 데이터를 통해 멤버 조회 OR 생성
        Member findOrCreateMember = memberService.findOrCreateMember(kakaoMemberData);

        // 토큰 생성 및 응답
        return ResponseEntity.ok(ApiResponse.onSuccess(memberTokenService.generateToken(findOrCreateMember)));
    }
}
