package umc.plantory.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.plantory.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom{
    Optional<Member> findByProviderId(String sub);
    @Query("SELECT m.nickname FROM Member m WHERE m.id = :memberId")
    String findNicknameById(@Param("memberId") Long memberId);

}