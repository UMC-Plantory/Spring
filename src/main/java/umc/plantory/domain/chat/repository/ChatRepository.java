package umc.plantory.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.chat.entity.Chat;
import umc.plantory.domain.member.entity.Member;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    void deleteByMember(Member member);
    void deleteAllByMember(Member member);
    List<Chat> findByMemberAndContentContainingIgnoreCaseOrderByCreatedAtDesc(Member member, String keyword);
}
