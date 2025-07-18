package umc.plantory.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.mapping.MemberTerm;
import umc.plantory.domain.term.entity.Term;

import java.util.List;
import java.util.Optional;

public interface MemberTermRepository extends JpaRepository<MemberTerm, Long> {
    // 회원 한 명의 약관 동의 전체 조회
    List<MemberTerm> findAllByMember(Member member);

    // 특정 약관 및 유저의 동의 상태 조회
    Optional<MemberTerm> findByMemberAndTerm(Member member, Term term);
}
