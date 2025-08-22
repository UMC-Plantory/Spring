package umc.plantory.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import umc.plantory.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        // String -> Long parsing
        Long memberId = Long.parseLong(username);

        // 실제 존재하느지 확인
        // MemberHandler 로 예외를 터트릴 경우 Spring Security 가 "인증 실패" 인식을 못함 -> UsernameNotFoundException 사용해야만 인식
        if (!memberRepository.existsById(memberId)) throw new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다. ID: " + memberId);

        return new CustomUserDetails(memberId);
    }
}
