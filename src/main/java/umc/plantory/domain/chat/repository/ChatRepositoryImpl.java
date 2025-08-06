package umc.plantory.domain.chat.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import umc.plantory.domain.chat.entity.Chat;
import umc.plantory.domain.chat.entity.QChat;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRepositoryImpl implements ChatRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Chat> findChats(Long memberId, LocalDateTime cursor, int size) {
        QChat chat = QChat.chat;

        BooleanBuilder builder = new BooleanBuilder()
                .and(chat.member.id.eq(memberId));

        if (cursor != null) {
            builder.and(chat.createdAt.lt(cursor));
        }

        return queryFactory
                .selectFrom(chat)
                .where(builder)
                .orderBy(chat.createdAt.desc())
                .limit(size + 1)
                .fetch();
    }
}
