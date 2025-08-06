package umc.plantory.domain.token.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import umc.plantory.domain.token.provider.JwtProvider;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // 토큰 없이도 가능하게
    private static final List<String> WHITE_LIST = List.of(
            "/swagger-ui", "/swagger-ui/", "/swagger-ui/**",
            "/v3/api-docs", "/v3/api-docs/**",
            "/auth/**",
            "/v1/plantory/member/kko/login"
    );

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        // 화이트리스트 경로면 필터 적용하지 않고 넘김
        if (isWhiteListed(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        Long memberId = jwtProvider.getMemberIdAndValidateToken(request.getHeader("Authorization"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(memberId.toString());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private boolean isWhiteListed(String uri) {
        return WHITE_LIST.stream().anyMatch(uri::startsWith);
    }
}
