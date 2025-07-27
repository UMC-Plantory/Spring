package umc.plantory.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.plantory.domain.member.dto.MemberRequestDTO;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.service.MemberCommandService;
import umc.plantory.global.apiPayload.ApiResponse;

@RestController
@RequestMapping("/v1/plantory/member")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 관련 API")
public class MemberRestController {
    private final MemberCommandService memberCommandService;

    @PostMapping("/term")
    @Operation(summary = "약관 동의 API", description = "회원이 약관에 동의하는 API입니다.")
    public ResponseEntity<ApiResponse<MemberResponseDTO.TermAgreementResponse>> termAgreement(
            @RequestBody MemberRequestDTO.TermAgreementRequest request) {
        return ResponseEntity.ok(ApiResponse.onSuccess(memberCommandService.termAgreement(request)));
    }

    @PatchMapping("/signup")
    @Operation(summary = "회원가입 완료 API", description = "회원의 추가 정보를 입력하여 회원가입을 완료하는 API입니다.")
    public ResponseEntity<ApiResponse<MemberResponseDTO.MemberSignupResponse>> signup(
            @RequestBody MemberRequestDTO.MemberSignupRequest request) {
        return ResponseEntity.ok(ApiResponse.onSuccess(memberCommandService.memberSignup(request)));
    }
}
