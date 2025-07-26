package umc.plantory.domain.terrarium.service;

import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;

public interface TerrariumCommandUseCase {
    TerrariumResponseDto.WateringTerrariumResponse performTerrariumWatering(Long memberId, Long terrariumId);
}

