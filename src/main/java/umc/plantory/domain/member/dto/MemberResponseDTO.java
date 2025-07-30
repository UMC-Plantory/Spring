package umc.plantory.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

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
    public static class MemberLogoutResponse {
        private Long memberId;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberDeleteResponse {
        private Long memberId;
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
    public static class KkoOAuth2LoginResponse {
        private String accessToken;
        private String refreshToken;
        private LocalDateTime accessTokenExpiredAt;
    }
}
