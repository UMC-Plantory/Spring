package umc.plantory.domain.terrarium.converter;

import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto.TerrariumResponse;

public class TerrariumConverter {

    public static TerrariumResponse toTerrariumResponse(String flowerImgUrl, int terrariumWateringCount, int memberWateringCount) {
        return TerrariumResponse.builder()
                .flowerImgUrl(flowerImgUrl)
                .terrariumWateringCount(terrariumWateringCount)
                .memberWateringCount(memberWateringCount)
                .build();
    }
}
