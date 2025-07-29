package umc.plantory.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class MemberResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KkoOAuth2LoginResponse {
        private String accessToken;
        private String refreshToken;
        private LocalDateTime accessTokenExpiredAt;
    }
}
