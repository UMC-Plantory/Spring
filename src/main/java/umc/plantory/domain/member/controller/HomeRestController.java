package umc.plantory.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.service.MemberQueryUseCase;
import umc.plantory.global.apiPayload.ApiResponse;

import java.time.YearMonth;

@RestController
@RequestMapping("/v1/plantory/home")
@RequiredArgsConstructor
@Tag(name = "Home", description = "홈화면 관련 API")
public class HomeRestController {
    private final MemberQueryUseCase memberQueryUseCase;

    @GetMapping
    @Operation(summary = "홈화면 조회 API", description = "홈화면 정보를 조회하는 API입니다. year_month가 없으면 이번 달 정보를 반환합니다.")
    public ResponseEntity<ApiResponse<MemberResponseDTO.HomeResponse>> getHome(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(value = "year_month", required = false) String yearMonth) {

        // year_month가 없으면 이번 달로 설정
        YearMonth parsedYearMonth = yearMonth != null ? YearMonth.parse(yearMonth) : YearMonth.now();

        return ResponseEntity.ok(ApiResponse.onSuccess(memberQueryUseCase.getHome(authorization, parsedYearMonth)));
    }
} 