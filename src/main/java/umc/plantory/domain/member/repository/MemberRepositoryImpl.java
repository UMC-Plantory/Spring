package umc.plantory.domain.member.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import umc.plantory.domain.member.entity.QMember;
import umc.plantory.domain.wateringCan.entity.QWateringCan;
import umc.plantory.global.enums.MemberStatus;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public long resetStreak(LocalDate yesterday) {
        QMember member = QMember.member;
        QWateringCan wateringCan = QWateringCan.wateringCan;

        // 어제 물뿌리개가 존재하지 않는 멤버 조회
        BooleanExpression noWateringYesterday = JPAExpressions
                .selectOne()
                .from(wateringCan)
                .where(
                        wateringCan.member.id.eq(member.id),
                        wateringCan.diaryDate.eq(yesterday)
                )
                .notExists();

        // 상태가 ACTIVE 이고, 연속 기록이 0보다 큰 멤버 중
        // 어제 물뿌리개가 존재하지 않으면 (어제 일기 작성 X) 연속 기록 0으로 초기화
        long updated = queryFactory
                .update(member)
                .set(member.continuousRecordCnt, 0)
                .where(
                        member.status.eq(MemberStatus.ACTIVE),
                        member.continuousRecordCnt.gt(0),
                        noWateringYesterday
                )
                .execute();

        return updated;
    }
}
