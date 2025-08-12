package umc.plantory.domain.member.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import umc.plantory.domain.member.entity.QMember;
import umc.plantory.global.enums.MemberStatus;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public long bulkUpdateContinuousRecordCnt(LocalDate yesterday) {
        QMember member = QMember.member;

        // 어제 일기를 작성하지 않은 ACTIVE 상태의 유저 연속 기록 0으로 업데이트
        return queryFactory
                .update(member)
                .set(member.continuousRecordCnt, 0)
                .where(
                        member.status.eq(MemberStatus.ACTIVE),
                        member.continuousRecordCnt.gt(0),
                        member.lastDiaryDate.ne(yesterday)
                )
                .execute();
    }
}
