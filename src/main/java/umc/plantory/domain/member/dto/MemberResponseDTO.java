package umc.plantory.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.plantory.global.enums.Emotion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MemberResponseDTO {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TermAgreementResponse {
        private Long memberId;
        private String message;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberSignupResponse {
        private Long memberId;
        private String nickname;
        private String userCustomId;
        private String profileImgUrl;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProfileResponse {
        private String userCustomId;
        private String nickname;
        private String email;
        private String gender;
        private String birth;
        private String profileImgUrl;
        private Integer wateringCanCnt;
        private Integer continuousRecordCnt;
        private Integer totalRecordCnt;
        private Integer avgSleepTime;
        private Integer totalBloomCnt;
        private String status;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProfileUpdateResponse {
        private Long memberId;
        private String nickname;
        private String userCustomId;
        private String gender;
        private String birth;
        private String profileImgUrl;
        private String message;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KkoOAuth2LoginResponse {
        private String accessToken;
        private String refreshToken;
        private LocalDateTime accessTokenExpireAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshAccessTokenResponse {
        private String accessToken;
        private LocalDateTime accessTokenExpireAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HomeResponse {

        private String userCustomId;

        private Long diaryId;           // 선택한 날짜의 일기 ID
        private LocalDate date;         // 선택한 날짜
        private Emotion emotion;        // 선택한 날짜 감정
        private String title;           // 선택한 날짜 일기 제목
        private Boolean isExist;        // 선택한 날짜에 일기 존재 여부

        private Integer year;           // 달력 연도
        private Integer month;          // 달력 월

        private List<CalendarEmotion> calendarEmotions;  // 한 달 전체 감정 정보

        private String flowerName;
        private String flowerStage;
        private Integer growthRate;
        private Integer continuousRecordCnt;

        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class CalendarEmotion {
            private LocalDate date;
            private Long diaryId;     // 클릭 시 사용할 수 있음
            private Boolean isExist;
            private Emotion emotion;  // 없을 경우 null
        }
    }
}
