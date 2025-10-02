package umc.plantory.domain.push.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.push.entity.PushData;

import java.util.Optional;

public interface PushRepository extends JpaRepository<PushData, Long> {
    Optional<PushData> findByMember(Member member);
}
