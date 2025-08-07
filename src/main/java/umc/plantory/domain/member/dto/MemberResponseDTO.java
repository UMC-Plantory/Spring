package umc.plantory.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.plantory.global.enums.Emotion;
import umc.plantory.global.enums.MemberStatus;

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
        private MemberStatus status;
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
        private MemberStatus status;
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
        private Integer wateringProgress; // 테라리움 진행도
        private Integer continuousRecordCnt; // 일기 연속 작성 횟수
        private List<DiaryDate> diaryDates; // 일기가 존재하는 날짜 정보

        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class DiaryDate {
            private LocalDate date;
            private Emotion emotion;
        }
    }
}
