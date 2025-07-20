package umc.plantory.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.global.enums.Provider;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);
    Optional<Member> findByNickname(String nickname);

    @Query("select m.wateringCanCnt from Member m where m.id = :memberId")
    Integer findWateringCanCntById(@Param("memberId") Long memberId);

}
