package umc.plantory.domain.wateringCan.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.wateringCan.entity.QWateringCan;
import umc.plantory.domain.wateringCan.entity.QWateringEvent;
import umc.plantory.domain.wateringCan.entity.WateringCan;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WateringCanRepositoryImpl implements WateringCanRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<WateringCan> findSelectedWateringCan(Member member) {
        QWateringCan wateringCan = QWateringCan.wateringCan;
        QWateringEvent wateringEvent = QWateringEvent.wateringEvent;

        // 조건 1. Member 일치
        // 조건 2. 조건 1과 일치하는 WateringCan 중 WateringEvent에 있지 않은 WateringCan
        BooleanBuilder builder = new BooleanBuilder()
                .and(wateringCan.member.eq(member))
                .and(
                        JPAExpressions
                                .selectOne()
                                .from(wateringEvent)
                                .where(wateringEvent.wateringCan.eq(wateringCan))
                                .notExists()
                );

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(wateringCan)
                        .where(builder)
                        .orderBy(wateringCan.diaryDate.asc())
                        .fetchFirst()
        );
    }
}
