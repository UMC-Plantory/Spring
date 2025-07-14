package umc.plantory.domain.chat.adapter.out.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.chat.entity.Chat;
import umc.plantory.domain.member.entity.Member;

import java.util.List;

public interface ChatJpaRepository extends JpaRepository<Chat, Long> {
    List<Chat> findTop10ByMemberOrderByCreatedAtDesc(Member member);
}
