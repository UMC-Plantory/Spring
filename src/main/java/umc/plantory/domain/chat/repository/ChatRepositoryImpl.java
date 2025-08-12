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

        // 기본 조건: 특정 회원의 채팅만 조회
        BooleanBuilder builder = new BooleanBuilder()
                .and(chat.member.id.eq(memberId));

        // 커서 기반 페이징 조건 추가
        // cursor가 null이 아닌 경우, 해당 시간보다 이전(더 오래된) 데이터만 조회
        // 이를 통해 중복 데이터 없이 연속적인 페이지 조회 가능
        if (cursor != null) {
            builder.and(chat.createdAt.lt(cursor));
        }

        return queryFactory
                .selectFrom(chat)
                .where(builder)
                .orderBy(chat.createdAt.desc()) // 최신 데이터부터 내림차순 정렬
                .limit(size + 1) // 페이징을 위해 요청된 크기보다 1개 더 조회
                // size + 1개를 조회하는 이유:
                // 1. 다음 페이지 존재 여부 확인을 위해
                // 2. 서비스 레이어에서 마지막 1개를 제거하고 실제 요청된 크기만 반환
                .fetch();
    }
}
