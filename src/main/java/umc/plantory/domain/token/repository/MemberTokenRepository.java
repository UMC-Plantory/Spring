package umc.plantory.domain.token.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.token.entity.MemberToken;

import java.util.Optional;

public interface MemberTokenRepository extends JpaRepository<MemberToken, Long>{
    Optional<MemberToken> findByMember(Member member);
}
