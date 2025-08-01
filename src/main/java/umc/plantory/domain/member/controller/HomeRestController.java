package umc.plantory.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.service.MemberQueryUseCase;
import umc.plantory.global.apiPayload.ApiResponse;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1/plantory/home")
@RequiredArgsConstructor
@Tag(name = "Home", description = "홈화면 관련 API")
public class HomeRestController {
    private final MemberQueryUseCase memberQueryUseCase;

    @GetMapping
    @Operation(summary = "홈화면 조회 API", description = "홈화면 정보를 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<MemberResponseDTO.HomeResponse>> getHome(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "selected_date", required = false) String selectedDate) {

        // selected_date를 LocalDate로 파싱 (null이면 null)
        LocalDate date = null;
        if (selectedDate != null && !selectedDate.isEmpty()) {
            date = LocalDate.parse(selectedDate);
        }

        return ResponseEntity.ok(ApiResponse.onSuccess(memberQueryUseCase.getHome(authorization, date)));
    }
} 