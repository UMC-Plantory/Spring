package umc.plantory.domain.terrarium.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTerrarium is a Querydsl query type for Terrarium
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTerrarium extends EntityPathBase<Terrarium> {

    private static final long serialVersionUID = -1224536691L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTerrarium terrarium = new QTerrarium("terrarium");

    public final umc.plantory.global.baseEntity.QBaseEntity _super = new umc.plantory.global.baseEntity.QBaseEntity(this);

    public final DateTimePath<java.time.LocalDateTime> bloomAt = createDateTime("bloomAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> firstStepDate = createDateTime("firstStepDate", java.time.LocalDateTime.class);

    public final umc.plantory.domain.flower.entity.QFlower flower;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isBloom = createBoolean("isBloom");

    public final umc.plantory.domain.member.entity.QMember member;

    public final DateTimePath<java.time.LocalDateTime> secondStepDate = createDateTime("secondStepDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> startAt = createDateTime("startAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> thirdStepDate = createDateTime("thirdStepDate", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QTerrarium(String variable) {
        this(Terrarium.class, forVariable(variable), INITS);
    }

    public QTerrarium(Path<? extends Terrarium> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTerrarium(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTerrarium(PathMetadata metadata, PathInits inits) {
        this(Terrarium.class, metadata, inits);
    }

    public QTerrarium(Class<? extends Terrarium> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.flower = inits.isInitialized("flower") ? new umc.plantory.domain.flower.entity.QFlower(forProperty("flower")) : null;
        this.member = inits.isInitialized("member") ? new umc.plantory.domain.member.entity.QMember(forProperty("member")) : null;
    }

}

