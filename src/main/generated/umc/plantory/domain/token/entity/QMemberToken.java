package umc.plantory.domain.token.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberToken is a Querydsl query type for MemberToken
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberToken extends EntityPathBase<MemberToken> {

    private static final long serialVersionUID = 1482691155L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberToken memberToken = new QMemberToken("memberToken");

    public final umc.plantory.global.baseEntity.QBaseEntity _super = new umc.plantory.global.baseEntity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> expireAt = createDateTime("expireAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final umc.plantory.domain.member.entity.QMember member;

    public final StringPath refreshToken = createString("refreshToken");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMemberToken(String variable) {
        this(MemberToken.class, forVariable(variable), INITS);
    }

    public QMemberToken(Path<? extends MemberToken> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberToken(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberToken(PathMetadata metadata, PathInits inits) {
        this(MemberToken.class, metadata, inits);
    }

    public QMemberToken(Class<? extends MemberToken> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new umc.plantory.domain.member.entity.QMember(forProperty("member")) : null;
    }

}

