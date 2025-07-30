package umc.plantory.domain.statistic.controller;

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

@RestController
@RequestMapping("/v1/plantory")
@RequiredArgsConstructor
public class StatisticRestController {

    private final StatisticQueryUseCase statisticQueryUseCase;

    @GetMapping("/statistic/weekly")
    public ResponseEntity<ApiResponse<StatisticResponseDTO.WeeklySleepStatisticDTO>> getWeeklySleepStatistic(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate today) {

        return ResponseEntity.ok(ApiResponse.onSuccess(statisticQueryUseCase.getWeeklySleepStatistics(today)));
    }


    @GetMapping("/statistic/monthly")
    public ResponseEntity<ApiResponse<StatisticResponseDTO.MonthlySleepStatisticDTO>> getMonthlySleepStatistic(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate today) {

        return ResponseEntity.ok(ApiResponse.onSuccess(statisticQueryUseCase.getMonthlySleepStatistics(today)));
    }
}
