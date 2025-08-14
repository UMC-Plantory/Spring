package umc.plantory.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.plantory.global.enums.Gender;

import java.time.LocalDate;
import java.util.List;

public class MemberRequestDTO {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TermAgreementRequest {
        @NotNull(message = "동의 약관 목록은 null일 수 없습니다.")
        private List<Long> agreeTermIdList;
        @NotNull(message = "비동의 약관 목록은 null일 수 없습니다. 빈 배열로 넘겨주세요.")
        private List<Long> disagreeTermIdList;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberSignupRequest {
        private Long memberId;
        @NotNull(message = "닉네임은 필수입니다.")
        @Size(max = 25, message = "닉네임은 최대 25자입니다.")
        private String nickname;
        @NotNull(message = "사용자 커스텀 ID는 필수입니다.")
        private String userCustomId;
        @NotNull(message = "성별은 필수입니다.")
        private Gender gender;
        @NotNull(message = "생년월일은 필수입니다.")
        private LocalDate birth;
        @NotNull(message = "프로필 이미지는 필수입니다.")
        private String profileImgUrl;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProfileUpdateRequest {
        @NotNull(message = "닉네임은 필수입니다.")
        @Size(max = 25, message = "닉네임은 최대 25자입니다.")
        private String nickname;
        @NotNull(message = "사용자 커스텀 ID는 필수입니다.")
        private String userCustomId;
        @NotNull(message = "성별은 필수입니다.")
        private Gender gender;
        @NotNull(message = "생년월일은 필수입니다.")
        private LocalDate birth;
        @NotNull(message = "프로필 이미지는 필수입니다.")
        private String profileImgUrl;
        @NotNull(message = "이미지 삭제 플래그는 필수입니다.")
        private Boolean deleteProfileImg; // 이미지 삭제 플래그 추가
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KkoOAuth2LoginRequest {
        @NotNull(message = "idToken 은 필수입니다.")
        private String idToken;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshAccessTokenRequest {
        @NotNull(message = "refreshToken 은 필수입니다.")
        private String refreshToken;
    }
}
