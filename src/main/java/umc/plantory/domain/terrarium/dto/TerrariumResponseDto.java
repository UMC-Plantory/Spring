package umc.plantory.domain.terrarium.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.plantory.global.enums.Emotion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class TerrariumResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor
    public static class TerrariumResponse{
        private Long terrariumId; // 테라리움 식별자
        private Integer terrariumWateringCount; // WateringEvent 엔티티 내 terrarium_id로 조회한 물 뿌리개 갯수
        private Integer memberWateringCount; // member 엔티티 내 watering_can_cnt 필드
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY) // 동적 응답을 위해 추가
    public static class WateringTerrariumResponse{
        private String nickname;
        private Integer terrariumWateringCountAfterEvent;
        private Integer memberWateringCountAfterEvent;
        private Map<Emotion, Integer> emotionList;
        private String flowerName;
        private Emotion flowerEmotion;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class CompletedTerrariumResponse{
        private Long terrariumId;
        private String nickname;
        private LocalDateTime bloomAt;
        private String flowerName;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class CompletedTerrariumDetailResponse {
        private LocalDate startAt;
        private LocalDate bloomAt;
        private Emotion mostEmotion;
        private List<LocalDate> usedDiaries;
        private LocalDate firstStepDate;
        private LocalDate secondStepDate;
        private LocalDate thirdStepDate;
    }
}
