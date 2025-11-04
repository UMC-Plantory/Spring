package umc.plantory.domain.token.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.apple.converter.AppleConverter;
import umc.plantory.domain.apple.entity.AppleAuthData;
import umc.plantory.domain.apple.repository.AppleAuthDataRepository;
import umc.plantory.domain.apple.sevice.AppleOidcService;
import umc.plantory.domain.kakao.converter.KakaoConverter;
import umc.plantory.domain.member.dto.MemberRequestDTO;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.token.converter.MemberTokenConverter;
import umc.plantory.domain.token.entity.MemberToken;
import umc.plantory.domain.token.provider.JwtProvider;
import umc.plantory.domain.token.repository.MemberTokenRepository;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.AppleAuthDataHandler;
import umc.plantory.global.apiPayload.exception.handler.JwtHandler;
import umc.plantory.global.scheduler.SchedulerJob;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberTokenCommandService implements MemberTokenCommandUseCase {
    private final MemberTokenRepository memberTokenRepository;
    private final AppleAuthDataRepository appleAuthDataRepository;
    private final SchedulerJob schedulerJob;
    private final JwtProvider jwtProvider;
    private final AppleOidcService appleOidcService;

    /**
     * KKO 유저 데이터를 통해 Access/Refresh 토큰 생성하는 메서드
     */
    @Override
    @Transactional
    public MemberResponseDTO.KkoOAuth2LoginResponse generateKkoLoginToken(Member member) {
        MemberToken findMemberToken = memberTokenRepository.findByMember(member)
                .orElse(null);

        // Access Token 생성
        String accessToken = jwtProvider.generateAccessToken(member);
        // Refresh Token 생성
        String refreshToken = jwtProvider.generateRefreshToken(member);
        // Access Token 만료시간
        LocalDateTime accessTokenExpireAt = jwtProvider.getExpiredAt(accessToken);
        // Refresh Token 만료 시간
        LocalDateTime refreshTokenExpireAt = jwtProvider.getExpiredAt(refreshToken);

        // 이미 MemberToken 이 있다면 UPDATE, 없다면 INSERT
        if (findMemberToken == null) {
            // MemberToken 에 저장
            memberTokenRepository.save(MemberTokenConverter.toMemberTokenForKakao(member, refreshToken, refreshTokenExpireAt));
        } else {
            // MemberToken Update
            findMemberToken.updateRefreshTokenAndExpireAt(refreshToken, refreshTokenExpireAt, "");
            memberTokenRepository.save(findMemberToken);
        }

        return KakaoConverter.toKkoOAuth2LoginResponse(member, accessToken, refreshToken, accessTokenExpireAt);
    }

    /**
     * APPLE 유저 데이터를 통해 Access/Refresh 토큰 생성하는 메서드
     */
    @Override
    @Transactional
    public MemberResponseDTO.AppleOauth2LoginResponse generateAppleLoginToken(Member member, String authorizationCode) {
        MemberToken findMemberToken = memberTokenRepository.findByMember(member)
                .orElse(null);
        AppleAuthData appleAuthData = appleAuthDataRepository.findByTag("plantory")
                .orElseThrow(() -> new AppleAuthDataHandler(ErrorStatus.NOT_FOUND_AUTH_DATA));

        // Access Token 생성
        String accessToken = jwtProvider.generateAccessToken(member);
        // Refresh Token 생성
        String refreshToken = jwtProvider.generateRefreshToken(member);
        // Access Token 만료시간
        LocalDateTime accessTokenExpireAt = jwtProvider.getExpiredAt(accessToken);
        // Refresh Token 만료 시간
        LocalDateTime refreshTokenExpireAt = jwtProvider.getExpiredAt(refreshToken);

        String appleClientSecret = appleAuthData.getClientSecret();
        if (appleClientSecret == null) {
            appleClientSecret = schedulerJob.refreshAppleClientSecret();
        }
        // Apple Refresh Token
        String appleRefreshToken = appleOidcService.createAppleRefreshToken(authorizationCode, appleClientSecret);

        // 이미 MemberToken 이 있다면 UPDATE, 없다면 INSERT
        if (findMemberToken == null) {
            // MemberToken 에 저장
            memberTokenRepository.save(MemberTokenConverter.toMemberTokenForApple(member, refreshToken, refreshTokenExpireAt, appleRefreshToken));
        } else {
            // MemberToken Update
            findMemberToken.updateRefreshTokenAndExpireAt(refreshToken, refreshTokenExpireAt, appleRefreshToken);
            memberTokenRepository.save(findMemberToken);
        }

        return AppleConverter.toAppleOauth2LoginResponse(member, accessToken, refreshToken, accessTokenExpireAt);
    }

    /**
     * 토큰 재발급 메서드
     *
     * 1. RefreshToken이 만료되었는지 확인 -> MemberToken 테이블 이용
     * 2. 만료되지 않았다면 액세스 토큰 발급
     * 3. 만료되었다면 재로그인 필요하다고 리턴
     */
    @Override
    @Transactional
    public MemberResponseDTO.RefreshAccessTokenResponse refreshAccessToken(MemberRequestDTO.RefreshAccessTokenRequest request) {
        MemberToken findMemberToken = memberTokenRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new JwtHandler(ErrorStatus.INVALID_REFRESH_TOKEN));

        // 만료된 리프레시 토큰일 경우 예외처리
        if (findMemberToken.getExpireAt().isBefore(LocalDateTime.now())) throw new JwtHandler(ErrorStatus.EXPIRED_REFRESH_TOKEN);

        String newAccessToken = jwtProvider.generateAccessToken(findMemberToken.getMember());
        LocalDateTime accessTokenExpiredAt = jwtProvider.getExpiredAt(newAccessToken);

        return MemberTokenConverter.toRefreshAccessTokenResponse(newAccessToken, accessTokenExpiredAt);
    }
}
