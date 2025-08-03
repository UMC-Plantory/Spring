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
import java.time.YearMonth;

@RestController
@RequestMapping("/v1/plantory/home")
@RequiredArgsConstructor
@Tag(name = "Home", description = "홈화면 관련 API")
public class HomeRestController {
    private final MemberQueryUseCase memberQueryUseCase;

    @GetMapping
    @Operation(summary = "홈화면 조회 API", description = "특정 월의 홈화면 정보를 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<MemberResponseDTO.HomeResponse>> getHome(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "year_month", required = true) String yearMonth) {

        // year_month를 YearMonth로 파싱 (예: "2024-01")
        YearMonth parsedYearMonth = YearMonth.parse(yearMonth);

        return ResponseEntity.ok(ApiResponse.onSuccess(memberQueryUseCase.getHome(authorization, parsedYearMonth)));
    }

    @GetMapping("/daily")
    @Operation(summary = "특정 날짜 일기 조회 API", description = "캘린더의 특정 날짜를 눌렀을 때 해당 날짜의 일기 정보를 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<MemberResponseDTO.DailyDiaryResponse>> getDailyDiary(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(value = "date", required = true) String date) {

        // date를 LocalDate로 파싱 
        LocalDate parsedDate = LocalDate.parse(date);

        return ResponseEntity.ok(ApiResponse.onSuccess(memberQueryUseCase.getDailyDiary(authorization, parsedDate)));
    }
} 