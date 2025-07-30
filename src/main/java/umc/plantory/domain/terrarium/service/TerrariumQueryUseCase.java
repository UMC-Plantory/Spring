package umc.plantory.domain.terrarium.service;

import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;

import java.util.List;

@Transactional(readOnly = true)
public interface TerrariumQueryUseCase {
    TerrariumResponseDto.TerrariumResponse findCurrentTerrariumData(String authorization);
    List<TerrariumResponseDto.CompletedTerrariumResponse> findCompletedTerrariumsByMonth(String authorization, int year, int month);
    TerrariumResponseDto.CompletedTerrariumDetatilResponse findCompletedTerrariumDetail(String authorization, Long terrariumId);
}
