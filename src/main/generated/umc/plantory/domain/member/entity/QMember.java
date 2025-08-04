package umc.plantory.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -1249797573L;

    public static final QMember member = new QMember("member1");

    public final umc.plantory.global.baseEntity.QBaseEntity _super = new umc.plantory.global.baseEntity.QBaseEntity(this);

    public final NumberPath<Integer> avgSleepTime = createNumber("avgSleepTime", Integer.class);

    public final DatePath<java.time.LocalDate> birth = createDate("birth", java.time.LocalDate.class);

    public final NumberPath<Integer> continuousRecordCnt = createNumber("continuousRecordCnt", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final EnumPath<umc.plantory.global.enums.Gender> gender = createEnum("gender", umc.plantory.global.enums.Gender.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> inactiveAt = createDateTime("inactiveAt", java.time.LocalDateTime.class);

    public final StringPath nickname = createString("nickname");

    public final StringPath profileImgUrl = createString("profileImgUrl");

    public final EnumPath<umc.plantory.global.enums.Provider> provider = createEnum("provider", umc.plantory.global.enums.Provider.class);

    public final StringPath providerId = createString("providerId");

    public final EnumPath<umc.plantory.global.enums.MemberRole> role = createEnum("role", umc.plantory.global.enums.MemberRole.class);

    public final EnumPath<umc.plantory.global.enums.MemberStatus> status = createEnum("status", umc.plantory.global.enums.MemberStatus.class);

    public final NumberPath<Integer> totalBloomCnt = createNumber("totalBloomCnt", Integer.class);

    public final NumberPath<Integer> totalRecordCnt = createNumber("totalRecordCnt", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final StringPath userCustomId = createString("userCustomId");

    public final NumberPath<Integer> wateringCanCnt = createNumber("wateringCanCnt", Integer.class);

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

