package umc.plantory.domain.terrarium.service;

import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.terrarium.dto.TerrariumResponseDto;

import java.time.YearMonth;
import java.util.List;

@Transactional(readOnly = true)
public interface TerrariumQueryUseCase {
    TerrariumResponseDto.TerrariumResponse findCurrentTerrariumData(String authorization);
    List<TerrariumResponseDto.CompletedTerrariumResponse> findCompletedTerrariumsByMonth(String authorization, YearMonth date);
    TerrariumResponseDto.CompletedTerrariumDetailResponse findCompletedTerrariumDetail(Long terrariumId);
}
