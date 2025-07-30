package umc.plantory.domain.token.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.plantory.domain.token.entity.TokenBlacklist;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    //토큰이 블랙리스트에 있는지 확인
    Optional<TokenBlacklist> findByToken(String token);
    
    //특정 토큰이 블랙리스트에 있는지 확인 (boolean 반환)
    boolean existsByToken(String token);
} 