package umc.plantory.domain.token.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.plantory.domain.member.dto.MemberRequestDTO;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.token.service.MemberTokenCommandUseCase;
import umc.plantory.global.apiPayload.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/plantory/token")
@Tag(name = "Token", description = "토큰 관련 API")
public class MemberTokenRestController {
    private final MemberTokenCommandUseCase memberTokenCommandUseCase;

    @PostMapping("/refresh")
    @Operation(summary = "토큰 재발급 API", description = "액세스 토큰 재발급 API 입니다.")
    public ResponseEntity<ApiResponse<MemberResponseDTO.RefreshAccessTokenResponse>> refreshAccessToken(@RequestBody MemberRequestDTO.RefreshAccessTokenRequest request) {

        return ResponseEntity.ok(ApiResponse.onSuccess(memberTokenCommandUseCase.refreshAccessToken(request)));
    }
}
