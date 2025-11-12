package umc.plantory.domain.apple.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import umc.plantory.global.baseEntity.BaseEntity;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@DynamicInsert
@DynamicUpdate
public class AppleAuthData extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apple_auth_data_id")
    private Long id;

    @Column(length = 2048)
    private String clientSecret;

    @Column(length = 10, nullable = false)
    @ColumnDefault("'plantory'")
    private String tag;

    public void updateClientSecret(String newClientSecret) {
        this.clientSecret = newClientSecret;
    }
}
