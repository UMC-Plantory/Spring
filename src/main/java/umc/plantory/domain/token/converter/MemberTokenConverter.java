package umc.plantory.domain.token.converter;

import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.token.entity.MemberToken;

import java.time.LocalDateTime;

public class MemberTokenConverter {

    public static MemberToken toMemberToken(Member member, String refreshToken, LocalDateTime expireAt) {
        return MemberToken.builder()
                .member(member)
                .refreshToken(refreshToken)
                .expireAt(expireAt)
                .build();
    }

    public static MemberResponseDTO.RefreshAccessTokenResponse toRefreshAccessTokenResponse (String accessToken, LocalDateTime accessTokenExpireAt) {
        return MemberResponseDTO.RefreshAccessTokenResponse.builder()
                .accessToken(accessToken)
                .accessTokenExpireAt(accessTokenExpireAt)
                .build();
    }
}
