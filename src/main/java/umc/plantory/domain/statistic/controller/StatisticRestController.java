package umc.plantory.domain.statistic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.plantory.domain.statistic.dto.response.StatisticResponseDTO;
import umc.plantory.domain.statistic.service.StatisticQueryUseCase;
import umc.plantory.global.apiPayload.ApiResponse;

import java.time.LocalDate;

@Tag(name = "Statistic", description = "통계 관련 API")
@RestController
@RequestMapping("/v1/plantory")
@RequiredArgsConstructor
public class StatisticRestController {

    private final StatisticQueryUseCase statisticQueryUseCase;

    @GetMapping("/sleep-stat/week")
    @Operation(
            summary = "최근 7일 수면 통계 조회",
            description = "입력한 날짜 기준으로 사용자의 최근 7일간 수면 통계 정보를 조회합니다."
    )
    public ResponseEntity<ApiResponse<StatisticResponseDTO.WeeklySleepStatisticDTO>> getWeeklySleepStatistic(
            @Parameter(description = "기준 날짜", example = "2025-07-22") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate today) {

        return ResponseEntity.ok(ApiResponse.onSuccess(statisticQueryUseCase.getWeeklySleepStatistics(today)));
    }

    @GetMapping("/sleep-stat/month")
    @Operation(
            summary = "최근 30일 수면 통계 조회",
            description = "입력한 날짜 기준으로 사용자의 최근 30일간 수면 통계 정보를 조회합니다."
    )
    public ResponseEntity<ApiResponse<StatisticResponseDTO.MonthlySleepStatisticDTO>> getMonthlySleepStatistic(
            @Parameter(description = "기준 날짜", example = "2025-07-22") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate today) {

        return ResponseEntity.ok(ApiResponse.onSuccess(statisticQueryUseCase.getMonthlySleepStatistics(today)));
    }
}
