package umc.plantory.domain.terrarium.converter;

import umc.plantory.domain.flower.entity.Flower;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.terrarium.dto.TerrariumResponseDto;
import umc.plantory.domain.terrarium.dto.TerrariumResponseDto.TerrariumResponse;
import umc.plantory.domain.terrarium.entity.Terrarium;

import java.time.LocalDate;
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
                .memberWateringCount(memberWateringCount)
                .terrariumWateringCount(terrariumWateringCount)
                .build();
    }

    public static Terrarium toTerrarium(Member member, Flower flower) {
        return Terrarium.builder()
                .member(member)
                .flower(flower)
                .startAt(LocalDateTime.now())
                .firstStepDate(LocalDate.now())
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

    public static TerrariumResponseDto.CompletedTerrariumResponse toCompletedTerrariumResponse(Long terrariumId,
                                                                                               LocalDateTime bloomAt,
                                                                                               String nickname,
                                                                                               String flowerImgUrl,
                                                                                               String flowerName) {
        return TerrariumResponseDto.CompletedTerrariumResponse.builder()
                .terrariumId(terrariumId)
                .bloomAt(bloomAt)
                .nickname(nickname)
                .flowerImgUrl(flowerImgUrl)
                .flowerName(flowerName)
                .build();
    }

    public static TerrariumResponseDto.CompletedTerrariumDetatilResponse toCompletedTerrariumDetatilResponse(Terrarium terrarium, List<LocalDate> usedDiaries) {
        return TerrariumResponseDto.CompletedTerrariumDetatilResponse.builder()
                .startAt(terrarium.getStartAt())
                .bloomAt(terrarium.getBloomAt())
                .mostEmotion(terrarium.getFlower().getEmotion())
                .firstStepDate(terrarium.getFirstStepDate())
                .secondStepDate(terrarium.getSecondStepDate())
                .thirdStepDate(terrarium.getThirdStepDate())
                .usedDiaries(usedDiaries)
                .build();
    }

}
