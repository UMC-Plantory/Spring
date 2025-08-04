package umc.plantory.domain.flower.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFlower is a Querydsl query type for Flower
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFlower extends EntityPathBase<Flower> {

    private static final long serialVersionUID = 457331709L;

    public static final QFlower flower = new QFlower("flower");

    public final EnumPath<umc.plantory.global.enums.Emotion> emotion = createEnum("emotion", umc.plantory.global.enums.Emotion.class);

    public final StringPath flowerImgUrl = createString("flowerImgUrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public QFlower(String variable) {
        super(Flower.class, forVariable(variable));
    }

    public QFlower(Path<? extends Flower> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFlower(PathMetadata metadata) {
        super(Flower.class, metadata);
    }

}

