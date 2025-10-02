package umc.plantory.domain.push.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.global.baseEntity.BaseEntity;
import umc.plantory.global.enums.Platform;
import umc.plantory.global.enums.TokenStatus;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@DynamicInsert
@DynamicUpdate
public class PushData extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "push_data_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Platform platform;

    @Column(length = 512, nullable = false)
    private String fcmToken;

    @Enumerated(EnumType.STRING)
    private TokenStatus tokenStatus;

    @Column(nullable = false)
    private LocalDateTime lastSeenAt;

    @Column(length = 64)
    private String lastError;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void updateFcmTokenAndStatus(String fcmToken) {
        this.fcmToken = fcmToken;
        this.tokenStatus = TokenStatus.VALID;
    }
}
