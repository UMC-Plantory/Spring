package umc.plantory.domain.terrarium.service;

import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;

import java.util.List;

@Transactional(readOnly = true)
public interface TerrariumQueryUseCase {
    TerrariumResponseDto.TerrariumResponse findCurrentTerrariumData(Long memberId);
    List<TerrariumResponseDto.CompletedTerrariumResponse> findCompletedTerrariumsByMonth(Long memberId, int year, int month);
    TerrariumResponseDto.CompletedTerrariumDetatilResponse findCompletedTerrariumDetail(Long memberId, Long terrariumId);
}
