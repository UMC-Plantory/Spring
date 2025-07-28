package umc.plantory.domain.token.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.kakao.converter.KakaoConverter;
import umc.plantory.domain.member.dto.MemberResponseDTO;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.token.converter.MemberTokenConverter;
import umc.plantory.domain.token.provider.JwtProvider;
import umc.plantory.domain.token.repository.MemberTokenRepository;

@Service
@RequiredArgsConstructor
public class MemberTokenCommandService implements MemberTokenCommandUseCase {
    private final MemberTokenRepository memberTokenRepository;
    private final JwtProvider jwtProvider;

    @Override
    @Transactional
    public MemberResponseDTO.KkoOAuth2LoginResponse generateToken(Member member) {
        // Access Token 생성
        String accessToken = jwtProvider.generateAccessToken(member);

        // Refresh Token 생성
        String refreshToken = jwtProvider.generateRefreshToken(member);

        // MemberToken 에 저장
        memberTokenRepository.save(MemberTokenConverter.toMemberToken(member, refreshToken, jwtProvider.getExpiredAt(refreshToken)));
        return KakaoConverter.toKkoOAuth2LoginResponse(accessToken, refreshToken);
    }
}
