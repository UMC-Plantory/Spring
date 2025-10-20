package umc.plantory.domain.token.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.global.baseEntity.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@DynamicInsert
@DynamicUpdate
public class MemberToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_token_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(length = 500, nullable = false, updatable = false)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime expireAt;

    @Column(length = 500)
    private String appleRefreshToken;

    public void updateRefreshTokenAndExpireAt (String refreshToken, LocalDateTime expireAt, String appleRefreshToken) {
        this.refreshToken = refreshToken;
        this.expireAt = expireAt;
        this.appleRefreshToken = appleRefreshToken;
    }
}
