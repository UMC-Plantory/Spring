package umc.plantory.domain.terrarium.converter;

import umc.plantory.domain.flower.entity.Flower;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.terrarium.dto.TerrariumResponseDto;
import umc.plantory.domain.terrarium.dto.TerrariumResponseDto.TerrariumResponse;
import umc.plantory.domain.terrarium.entity.Terrarium;
import umc.plantory.global.enums.Emotion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    public static TerrariumResponseDto.WateringTerrariumResponse toDefaultWateringTerrariumResponse(Integer terrariumWateringCountAfterEvent, Integer memberWateringCountAfterEvent) {
        return TerrariumResponseDto.WateringTerrariumResponse.builder()
                .terrariumWateringCountAfterEvent(terrariumWateringCountAfterEvent)
                .memberWateringCountAfterEvent(memberWateringCountAfterEvent)
                .build();
    }

    public static TerrariumResponseDto.WateringTerrariumResponse toBloomWateringTerrariumResponse(Integer terrariumWateringCountAfterEvent,
                                                                                                  Integer memberWateringCountAfterEvent,
                                                                                                  Map<Emotion, Integer> emotionList,
                                                                                                  Flower flower) {
        return TerrariumResponseDto.WateringTerrariumResponse.builder()
                .terrariumWateringCountAfterEvent(terrariumWateringCountAfterEvent)
                .memberWateringCountAfterEvent(memberWateringCountAfterEvent)
                .emotionList(emotionList)
                .flowerName(flower.getName())
                .flowerImgUrl(flower.getFlowerImgUrl())
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
