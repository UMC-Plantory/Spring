package umc.plantory.domain.member.mapping;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberTerm is a Querydsl query type for MemberTerm
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberTerm extends EntityPathBase<MemberTerm> {

    private static final long serialVersionUID = -1230347760L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberTerm memberTerm = new QMemberTerm("memberTerm");

    public final umc.plantory.global.baseEntity.QBaseEntity _super = new umc.plantory.global.baseEntity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isAgree = createBoolean("isAgree");

    public final umc.plantory.domain.member.entity.QMember member;

    public final umc.plantory.domain.term.entity.QTerm term;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMemberTerm(String variable) {
        this(MemberTerm.class, forVariable(variable), INITS);
    }

    public QMemberTerm(Path<? extends MemberTerm> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberTerm(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberTerm(PathMetadata metadata, PathInits inits) {
        this(MemberTerm.class, metadata, inits);
    }

    public QMemberTerm(Class<? extends MemberTerm> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new umc.plantory.domain.member.entity.QMember(forProperty("member")) : null;
        this.term = inits.isInitialized("term") ? new umc.plantory.domain.term.entity.QTerm(forProperty("term")) : null;
    }

}

