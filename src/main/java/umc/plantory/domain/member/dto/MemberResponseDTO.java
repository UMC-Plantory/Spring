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

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProfileResponse {
        private String userCustomId;
        private String nickname;

        private String profileImgUrl;
        private Integer continuousRecordCnt;
        private Integer totalRecordCnt;
        private Integer avgSleepTime;
        private Integer totalBloomCnt;
        // private String status;
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
        private String yearMonth;      // 'YYYY-MM'
        private Integer wateringCount;  // 현재 테라리움 물 준 횟수
        private Integer wateringProgress; // 테라리움 진행도
        private Integer continuousRecordCnt; // 일기 연속 작성 횟수
        private List<MonthlyDiary> monthlyDiaries; // 해당 월에 작성된 일기 데이터

        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class MonthlyDiary {
            private Long diaryId;
            private LocalDate diaryDate;
            private Emotion emotion;
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyDiaryResponse {
        private Long diaryId;
        private String title;
        private Emotion emotion;
    }
}
