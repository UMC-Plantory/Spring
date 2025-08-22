package umc.plantory.domain.terrarium.service;

import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.terrarium.dto.TerrariumResponseDto;

import java.time.YearMonth;

@Transactional(readOnly = true)
public interface TerrariumQueryUseCase {
    TerrariumResponseDto.TerrariumResponse findCurrentTerrariumData(String authorization);
    TerrariumResponseDto.TerrariumMonthlyListResponse findCompletedTerrariumsByMonth(String authorization, YearMonth date);
    TerrariumResponseDto.CompletedTerrariumDetailResponse findCompletedTerrariumDetail(String authorization, Long terrariumId);
}
