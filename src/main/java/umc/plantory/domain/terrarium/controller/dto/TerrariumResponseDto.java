package umc.plantory.domain.terrarium.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class TerrariumResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor
    public static class TerrariumResponse{
        private String flowerImgUrl; // Flower 엔티티 이미지
        private int terrariumWateringCount; // WateringEvent 엔티티 내 terrarium_id로 조회한 물 뿌리개 갯수
        private int memberWateringCount; // member 엔티티 내 watering_can_cnt 필드
    }
}
