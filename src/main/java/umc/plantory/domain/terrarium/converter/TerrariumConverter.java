package umc.plantory.domain.terrarium.converter;

import umc.plantory.domain.flower.entity.Flower;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto.TerrariumResponse;
import umc.plantory.domain.terrarium.entity.Terrarium;

import java.time.LocalDateTime;

public class TerrariumConverter {

    public static TerrariumResponse toTerrariumResponse(Long terrariumId, String flowerImgUrl, int terrariumWateringCount, int memberWateringCount) {
        return TerrariumResponse.builder()
                .terrariumId(terrariumId)
                .flowerImgUrl(flowerImgUrl)
                .terrariumWateringCount(terrariumWateringCount)
                .memberWateringCount(memberWateringCount)
                .build();
    }

    public static Terrarium toTerrarium(Member member, Flower flower) {
        return Terrarium.builder()
                .member(member)
                .flower(flower)
                .startAt(LocalDateTime.now())
                .isBloom(false)
                .build();

    }

}
