package umc.plantory.domain.statistic.service;

import umc.plantory.domain.statistic.dto.response.StatisticResponseDTO;

import java.time.LocalDate;

public interface StatisticQueryUseCase {
    StatisticResponseDTO.WeeklySleepStatisticDTO getWeeklySleepStatistics(LocalDate today);
}
