package umc.plantory.domain.terrarium.converter;

import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto.TerrariumResponse;

public class TerrariumConverter {

    public static TerrariumResponse toTerrariumResponse(Long terrariumId, String flowerImgUrl, int terrariumWateringCount, int memberWateringCount) {
        return TerrariumResponse.builder()
                .terrariumId(terrariumId)
                .flowerImgUrl(flowerImgUrl)
                .terrariumWateringCount(terrariumWateringCount)
                .memberWateringCount(memberWateringCount)
                .build();
    }

}
