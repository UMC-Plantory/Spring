package umc.plantory.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResponseDTO {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberProfile {
        private Long memberId;
        private String nickname;
        private String email;
        private String gender;
        private String birth;
        private String profileImgUrl;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberAuth {
        private Long userId;
        private String accessToken;
        private String refreshToken;
        private boolean isNewUser;
    }
} 