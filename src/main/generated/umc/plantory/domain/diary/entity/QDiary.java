package umc.plantory.domain.diary.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDiary is a Querydsl query type for Diary
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDiary extends EntityPathBase<Diary> {

    private static final long serialVersionUID = -1788845043L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDiary diary = new QDiary("diary");

    public final umc.plantory.global.baseEntity.QBaseEntity _super = new umc.plantory.global.baseEntity.QBaseEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final DatePath<java.time.LocalDate> diaryDate = createDate("diaryDate", java.time.LocalDate.class);

    public final EnumPath<umc.plantory.global.enums.Emotion> emotion = createEnum("emotion", umc.plantory.global.enums.Emotion.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final umc.plantory.domain.member.entity.QMember member;

    public final DateTimePath<java.time.LocalDateTime> sleepEndTime = createDateTime("sleepEndTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> sleepStartTime = createDateTime("sleepStartTime", java.time.LocalDateTime.class);

    public final EnumPath<umc.plantory.global.enums.DiaryStatus> status = createEnum("status", umc.plantory.global.enums.DiaryStatus.class);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QDiary(String variable) {
        this(Diary.class, forVariable(variable), INITS);
    }

    public QDiary(Path<? extends Diary> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDiary(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDiary(PathMetadata metadata, PathInits inits) {
        this(Diary.class, metadata, inits);
    }

    public QDiary(Class<? extends Diary> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new umc.plantory.domain.member.entity.QMember(forProperty("member")) : null;
    }

}

