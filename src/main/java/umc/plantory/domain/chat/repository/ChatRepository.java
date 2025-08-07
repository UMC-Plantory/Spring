package umc.plantory.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.chat.entity.Chat;
import umc.plantory.domain.member.entity.Member;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    // member의 최신 채팅 6개(최신 -> 과거) 조회
    List<Chat> findTop6ByMemberOrderByCreatedAtDesc(Member member);

    // 주어진 사용자의 지정 시간(before) 이전 채팅 메시지 중
    // 최신 순(내림차순)으로 6개를 조회
    List<Chat> findTop6ByMemberAndCreatedAtLessThanOrderByCreatedAtDesc(Member member, LocalDateTime before);


}
