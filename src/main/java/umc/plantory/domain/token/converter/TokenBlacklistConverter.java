package umc.plantory.domain.token.converter;

import umc.plantory.domain.token.entity.TokenBlacklist;

import java.time.LocalDateTime;

public class TokenBlacklistConverter {
    
    public static TokenBlacklist toTokenBlacklist(String token, LocalDateTime expiredAt, String reason) {
        return TokenBlacklist.builder()
                .token(token)
                .expiredAt(expiredAt)
                .reason(reason)
                .build();
    }
} 