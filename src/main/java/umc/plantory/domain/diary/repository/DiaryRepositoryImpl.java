package umc.plantory.domain.diary.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import umc.plantory.domain.diary.dto.DiaryRequestDTO;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.diary.entity.QDiary;
import umc.plantory.global.enums.DiaryStatus;
import umc.plantory.global.enums.Emotion;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DiaryRepositoryImpl implements DiaryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Diary> findFilteredDiaries(Long memberId, DiaryRequestDTO.DiaryFilterDTO request) {
        QDiary diary = QDiary.diary;

        // 해당 유저의 NORMAL, SCRAP 일기만 조회
        BooleanBuilder builder = new BooleanBuilder()
                .and(diary.member.id.eq(memberId))
                .and(diary.status.in(DiaryStatus.NORMAL, DiaryStatus.SCRAP));

        // 날짜 범위 필터
        YearMonth from = Optional.ofNullable(request.getFrom())
                .map(YearMonth::parse)
                .orElse(YearMonth.now());
        YearMonth to = Optional.ofNullable(request.getTo())
                .map(YearMonth::parse)
                .orElse(from);

        builder.and(diary.diaryDate.goe(from.atDay(1)));
        builder.and(diary.diaryDate.loe(to.atEndOfMonth()));

        // 감정 필터
        if (request.getEmotion() != null && !request.getEmotion().isEmpty()) {
            List<Emotion> emotions = request.getEmotion().stream()
                    .map(Emotion::valueOf)
                    .toList();
            builder.and(diary.emotion.in(emotions));
        }

        // 커서 조건 (diaryDate)
        if (request.getCursor() != null) {
            if ("oldest".equals(request.getSort())) {
                builder.and(diary.diaryDate.gt(request.getCursor()));
            } else {
                builder.and(diary.diaryDate.lt(request.getCursor()));
            }
        }

        // 정렬
        OrderSpecifier<?> order = "oldest".equals(request.getSort())
                ? diary.diaryDate.asc()
                : diary.diaryDate.desc();

        return queryFactory
                .selectFrom(diary)
                .where(builder)
                .orderBy(order)
                .limit(request.getSize() + 1)
                .fetch();
    }

    @Override
    public List<Diary> findScrappedDiaries(Long memberId, String sort, LocalDate cursor, int size) {
        QDiary diary = QDiary.diary;

        // 해당 유저의 SCRAP 일기만 조회
        BooleanBuilder builder = new BooleanBuilder()
                .and(diary.member.id.eq(memberId))
                .and(diary.status.eq(DiaryStatus.SCRAP));

        // 커서 조건 (diaryDate)
        if (cursor != null) {
            if ("oldest".equals(sort)) {
                builder.and(diary.diaryDate.gt(cursor));
            } else {
                builder.and(diary.diaryDate.lt(cursor));
            }
        }

        // 정렬
        OrderSpecifier<?> order = "oldest".equals(sort)
                ? diary.diaryDate.asc()
                : diary.diaryDate.desc();

        return queryFactory
                .selectFrom(diary)
                .where(builder)
                .orderBy(order)
                .limit(size + 1)
                .fetch();
    }
}
