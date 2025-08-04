package umc.plantory.domain.wateringCan.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWateringCan is a Querydsl query type for WateringCan
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWateringCan extends EntityPathBase<WateringCan> {

    private static final long serialVersionUID = -1368771123L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWateringCan wateringCan = new QWateringCan("wateringCan");

    public final umc.plantory.domain.diary.entity.QDiary diary;

    public final DatePath<java.time.LocalDate> diaryDate = createDate("diaryDate", java.time.LocalDate.class);

    public final EnumPath<umc.plantory.global.enums.Emotion> emotion = createEnum("emotion", umc.plantory.global.enums.Emotion.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final umc.plantory.domain.member.entity.QMember member;

    public QWateringCan(String variable) {
        this(WateringCan.class, forVariable(variable), INITS);
    }

    public QWateringCan(Path<? extends WateringCan> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWateringCan(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWateringCan(PathMetadata metadata, PathInits inits) {
        this(WateringCan.class, metadata, inits);
    }

    public QWateringCan(Class<? extends WateringCan> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.diary = inits.isInitialized("diary") ? new umc.plantory.domain.diary.entity.QDiary(forProperty("diary"), inits.get("diary")) : null;
        this.member = inits.isInitialized("member") ? new umc.plantory.domain.member.entity.QMember(forProperty("member")) : null;
    }

}

