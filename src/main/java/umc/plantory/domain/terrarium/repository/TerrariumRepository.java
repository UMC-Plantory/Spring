package umc.plantory.domain.terrarium.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.terrarium.entity.Terrarium;

import java.util.Optional;

public interface TerrariumRepository extends JpaRepository<Terrarium, Long> {
    Optional<Terrarium> findByMember(Member member);
} 