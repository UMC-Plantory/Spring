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
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import umc.plantory.domain.token.provider.JwtProvider;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // 토큰 없이도 가능하게
    private static final List<String> WHITE_LIST = List.of(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/auth/**",
            "/v1/plantory/members/auth/kko",
            "/v1/plantory/members/auth/apple",
            "/v1/plantory/auth/refresh",
            "/v1/plantory/push/**"
    );

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Long memberId = jwtProvider.getMemberIdAndValidateToken(request.getHeader("Authorization"));

            UserDetails userDetails = userDetailsService.loadUserByUsername(memberId.toString());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // 더 안정적인 AntPathMatcher.match() 로 수정
        return WHITE_LIST.stream().anyMatch(white -> antPathMatcher.match(white, request.getRequestURI()));
    }
}
