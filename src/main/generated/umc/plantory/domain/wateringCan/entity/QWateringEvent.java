package umc.plantory.domain.wateringCan.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWateringEvent is a Querydsl query type for WateringEvent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWateringEvent extends EntityPathBase<WateringEvent> {

    private static final long serialVersionUID = -1126589097L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWateringEvent wateringEvent = new QWateringEvent("wateringEvent");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final umc.plantory.domain.terrarium.entity.QTerrarium terrarium;

    public final QWateringCan wateringCan;

    public QWateringEvent(String variable) {
        this(WateringEvent.class, forVariable(variable), INITS);
    }

    public QWateringEvent(Path<? extends WateringEvent> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWateringEvent(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWateringEvent(PathMetadata metadata, PathInits inits) {
        this(WateringEvent.class, metadata, inits);
    }

    public QWateringEvent(Class<? extends WateringEvent> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.terrarium = inits.isInitialized("terrarium") ? new umc.plantory.domain.terrarium.entity.QTerrarium(forProperty("terrarium"), inits.get("terrarium")) : null;
        this.wateringCan = inits.isInitialized("wateringCan") ? new QWateringCan(forProperty("wateringCan"), inits.get("wateringCan")) : null;
    }

}

