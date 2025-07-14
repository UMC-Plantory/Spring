package umc.plantory.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
