package umc.plantory.global.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final Long memberId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 기본적으로 USER 역할 하나만 부여 (Admin 확장 가능)
        return Collections.singleton(() -> "ROLE_USER");
    }

    @Override
    public String getUsername() {
        return memberId.toString();
    }

    // 비밀번호는 사용하지 않기 때문에 null 리턴
    @Override public String getPassword() { return null; }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
