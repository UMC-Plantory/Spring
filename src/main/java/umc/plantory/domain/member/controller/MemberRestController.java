package umc.plantory.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.plantory.domain.member.dto.MemberRequestDTO;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.service.MemberCommandService;
import umc.plantory.global.apiPayload.ApiResponse;

@RestController
@RequestMapping("/v1/plantory/member")
@RequiredArgsConstructor
public class MemberRestController {
    private final MemberCommandService memberCommandService;

    @PatchMapping("/signup")
    public ResponseEntity<ApiResponse<MemberResponseDTO.MemberSignupResponse>> signup(@RequestBody MemberRequestDTO.MemberSignupRequest request) {
        return ResponseEntity.ok(ApiResponse.onSuccess(memberCommandService.memberSignup(request)));
    }
}
