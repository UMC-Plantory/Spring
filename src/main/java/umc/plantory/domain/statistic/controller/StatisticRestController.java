package umc.plantory.domain.statistic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.plantory.domain.statistic.dto.response.StatisticResponseDTO;
import umc.plantory.domain.statistic.service.StatisticQueryUseCase;
import umc.plantory.global.apiPayload.ApiResponse;

import java.time.LocalDate;

@Tag(name = "Statistic", description = "통계 관련 API")
@RestController
@RequestMapping("/v1/plantory/statistics")
@RequiredArgsConstructor
public class StatisticRestController {

    private final StatisticQueryUseCase statisticQueryUseCase;

    @GetMapping("/sleep/weekly")
    @Operation(
            summary = "최근 7일 수면 통계 조회",
            description = "입력한 날짜 기준으로 사용자의 최근 7일간 수면 통계 정보를 조회합니다."
    )
    public ResponseEntity<ApiResponse<StatisticResponseDTO.WeeklySleepStatisticDTO>> getWeeklySleepStatistic(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Parameter(description = "기준 날짜", example = "2025-07-22") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate today) {

        return ResponseEntity.ok(ApiResponse.onSuccess(statisticQueryUseCase.getWeeklySleepStatistics(authorization, today)));
    }

    @GetMapping("/sleep/monthly")
    @Operation(
            summary = "최근 30일 수면 통계 조회",
            description = "입력한 날짜 기준으로 사용자의 최근 30일간 수면 통계 정보를 조회합니다."
    )
    public ResponseEntity<ApiResponse<StatisticResponseDTO.MonthlySleepStatisticDTO>> getMonthlySleepStatistic(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Parameter(description = "기준 날짜", example = "2025-07-22") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate today) {

        return ResponseEntity.ok(ApiResponse.onSuccess(statisticQueryUseCase.getMonthlySleepStatistics(authorization, today)));
    }

    @GetMapping("/emotion/weekly")
    @Operation(
            summary = "최근 7일 감정 통계 조회",
            description = "입력한 날짜 기준으로 사용자의 최근 7일간 감정 통계 정보를 조회합니다."
    )
    public ResponseEntity<ApiResponse<StatisticResponseDTO.EmotionStatisticDTO>> getWeeklyEmotionStatistic(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Parameter(description = "기준 날짜", example = "2025-07-22") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate today) {

        return ResponseEntity.ok(ApiResponse.onSuccess(statisticQueryUseCase.getEmotionStatistics(authorization, today, 7)));
        }

    @GetMapping("/emotion/monthly")
    @Operation(
            summary = "최근 30일 감정 통계 조회",
            description = "입력한 날짜 기준으로 사용자의 최근 30일간 감정 통계 정보를 조회합니다."
    )
    public ResponseEntity<ApiResponse<StatisticResponseDTO.EmotionStatisticDTO>> getMonthlyEmotionStatistic(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Parameter(description = "기준 날짜", example = "2025-07-22") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate today) {

        return ResponseEntity.ok(ApiResponse.onSuccess(statisticQueryUseCase.getEmotionStatistics(authorization, today, 30)));
    }
}
