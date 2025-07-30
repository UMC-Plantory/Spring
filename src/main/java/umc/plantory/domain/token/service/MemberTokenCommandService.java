package umc.plantory.domain.token.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.kakao.converter.KakaoConverter;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.token.converter.MemberTokenConverter;
import umc.plantory.domain.token.converter.TokenBlacklistConverter;
import umc.plantory.domain.token.entity.TokenBlacklist;
import umc.plantory.domain.token.provider.JwtProvider;
import umc.plantory.domain.token.repository.MemberTokenRepository;
import umc.plantory.domain.token.repository.TokenBlacklistRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberTokenCommandService implements MemberTokenCommandUseCase {
    private final MemberTokenRepository memberTokenRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final JwtProvider jwtProvider;

    /**
     * 유저 데이터를 통해 Access/Refresh 토큰 생성하는 메서드
     */
    @Override
    @Transactional
    public MemberResponseDTO.KkoOAuth2LoginResponse generateToken(Member member) {
        // Access Token 생성
        String accessToken = jwtProvider.generateAccessToken(member);
        // Refresh Token 생성
        String refreshToken = jwtProvider.generateRefreshToken(member);
        // Access Token 만료시간
        LocalDateTime accessTokenExpiredAt = jwtProvider.getExpiredAt(accessToken);
        // Refresh Token 만료 시간
        LocalDateTime refreshTokenExpiredAt = jwtProvider.getExpiredAt(refreshToken);

        // MemberToken 에 저장
        memberTokenRepository.save(MemberTokenConverter.toMemberToken(member, refreshToken, refreshTokenExpiredAt));

        return KakaoConverter.toKkoOAuth2LoginResponse(accessToken, refreshToken, accessTokenExpiredAt);
    }

    /**
     * 토큰을 블랙리스트에 추가
     */
    @Transactional
    public void addToBlacklist(String token, String reason) {
        // 토큰의 만료 시간 가져오기
        LocalDateTime expiredAt = jwtProvider.getExpiredAt(token);
        
        // 블랙리스트에 추가
        TokenBlacklist blacklistedToken = TokenBlacklistConverter.toTokenBlacklist(token, expiredAt, reason);
        tokenBlacklistRepository.save(blacklistedToken);
    }

    /**
     * 토큰이 블랙리스트에 있는지 확인
     */
    @Transactional(readOnly = true)
    public boolean isBlacklisted(String token) {
        return tokenBlacklistRepository.existsByToken(token);
    }
}
