package umc.plantory.domain.terrarium.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.terrarium.entity.Terrarium;

import java.util.Optional;

public interface TerrariumJpaRepository extends JpaRepository<Terrarium, Long> {

    Optional<Long> findFlowerIdByMemberId(Long memberId);

    Terrarium findByMemberIdAndIsBloomFalse(Long memberId);
}
