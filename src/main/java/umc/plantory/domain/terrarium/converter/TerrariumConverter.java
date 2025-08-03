package umc.plantory.domain.terrarium.converter;

import umc.plantory.domain.flower.entity.Flower;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto.TerrariumResponse;
import umc.plantory.domain.terrarium.entity.Terrarium;

import java.time.LocalDateTime;
import java.util.List;

public class TerrariumConverter {

    public static TerrariumResponse toTerrariumResponse(Long terrariumId,
                                                        String flowerImgUrl,
                                                        int terrariumWateringCount,
                                                        int memberWateringCount) {
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
                .firstStepDate(LocalDateTime.now())
                .isBloom(false)
                .build();

    }

    public static TerrariumResponseDto.WateringTerrariumResponse toWateringTerrariumResponse(int terrariumWateringCount,
                                                                                             int memberWateringCount,
                                                                                             List<Object[]> emotionCounts,
                                                                                             Flower flower) {
        return TerrariumResponseDto.WateringTerrariumResponse.builder()
                .terrariumWateringCount(terrariumWateringCount)
                .memberWateringCount(memberWateringCount)
                .emotionCounts(emotionCounts)
                .flowerName(flower.getName())
                .flowerEmotion(flower.getEmotion())
                .build();
    }

}
