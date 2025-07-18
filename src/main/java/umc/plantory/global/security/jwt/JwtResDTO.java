package umc.plantory.global.security.jwt;

import lombok.Getter;


public class JwtResDTO {
    @Getter
    public static class Login {
        private final String accessToken;
        private final String refreshToken;

        public Login(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }
}
