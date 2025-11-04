package umc.plantory.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.mapping.MemberTerm;
import umc.plantory.domain.term.entity.Term;

import java.util.Optional;

public interface MemberTermRepository extends JpaRepository<MemberTerm, Long> {
    // 특정 약관 및 유저의 동의 상태 조회
    Optional<MemberTerm> findByMemberAndTerm(Member member, Term term);
    void deleteAllByMember(Member member);
}
