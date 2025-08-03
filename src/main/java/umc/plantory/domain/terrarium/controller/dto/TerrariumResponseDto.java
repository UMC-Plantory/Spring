package umc.plantory.domain.terrarium.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import umc.plantory.global.enums.Emotion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TerrariumResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor
    public static class TerrariumResponse{
        private Long terrariumId; // 테라리움 식별자
        private String flowerImgUrl; // Flower 엔티티 이미지
        private Integer terrariumWateringCount; // WateringEvent 엔티티 내 terrarium_id로 조회한 물 뿌리개 갯수
        private Integer memberWateringCount; // member 엔티티 내 watering_can_cnt 필드
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class WateringTerrariumResponse{
        private Integer terrariumWateringCount; // WateringEvent 엔티티 내 terrarium_id로 조회한 물 뿌리개 갯수
        private Integer memberWateringCount; // member 엔티티 내 watering_can_cnt 필드
        private List<Object[]> emotionCounts; // 물뿌리기 7회 이상 시에 emotionCountsList
        private String flowerName;
        private Emotion flowerEmotion;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class CompletedTerrariumResponse{
        private Long terrariumId;
        private LocalDateTime bloomAt;
        private String flowerImgUrl;
        private String flowerName;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class CompletedTerrariumDetatilResponse{
        private LocalDateTime startAt;
        private LocalDateTime bloomAt;
        private List<LocalDate> usedDiaries;
        private LocalDateTime firstStepDate;
        private LocalDateTime secondStepDate;
        private LocalDateTime thirdStepDate;
    }
}
