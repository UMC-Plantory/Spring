package umc.plantory.domain.statistic.service;

import umc.plantory.domain.statistic.dto.response.StatisticResponseDTO;

import java.time.LocalDate;

public interface StatisticQueryUseCase {
    StatisticResponseDTO.WeeklySleepStatisticDTO getWeeklySleepStatistics(String authorization, LocalDate today);

    StatisticResponseDTO.MonthlySleepStatisticDTO getMonthlySleepStatistics(String authorization, LocalDate today);

    StatisticResponseDTO.EmotionStatisticDTO getEmotionStatistics(String authorization, LocalDate today, Integer range);
}
