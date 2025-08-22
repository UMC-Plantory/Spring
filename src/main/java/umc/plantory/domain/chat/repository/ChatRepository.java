package umc.plantory.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.chat.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
