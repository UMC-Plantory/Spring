package umc.plantory.domain.chat.repository;

import umc.plantory.domain.chat.entity.Chat;
import java.time.LocalDateTime;
import java.util.List;

public interface ChatRepositoryCustom {
    List<Chat> findChats(Long memberId, LocalDateTime cursor, int size);
}
