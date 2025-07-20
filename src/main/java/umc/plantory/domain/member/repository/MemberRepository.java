package umc.plantory.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.global.enums.Provider;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);
    Optional<Member> findByNickname(String nickname);
}
