package umc.plantory.domain.terrarium.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.terrarium.entity.Terrarium;

import java.util.Optional;

public interface TerrariumJpaRepository extends JpaRepository<Terrarium, Long> {
    
    /**
     * 회원 ID로 꽃 ID 조회
     * 
     * @param memberId 회원 ID
     * @return 꽃 ID
     */
    Optional<Long> findFlowerIdByMemberId(Long memberId);
}
