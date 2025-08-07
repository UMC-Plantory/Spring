package umc.plantory.domain.terrarium.service;

import umc.plantory.domain.terrarium.dto.TerrariumResponseDto;

public interface TerrariumCommandUseCase {
    TerrariumResponseDto.WateringTerrariumResponse performTerrariumWatering(String authorization, Long terrariumId);
}

