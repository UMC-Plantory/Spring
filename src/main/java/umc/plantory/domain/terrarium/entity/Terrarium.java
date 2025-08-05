package umc.plantory.domain.terrarium.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import umc.plantory.domain.flower.entity.Flower;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.global.baseEntity.BaseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@DynamicInsert
@DynamicUpdate
public class Terrarium {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "terrarium_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flower_id")
    private Flower flower;

    @Column(nullable = false)
    private LocalDateTime startAt;

    private LocalDateTime bloomAt;

    @Column(nullable = false)
    private Boolean isBloom;

    @Column(nullable = false)
    private LocalDate firstStepDate;

    private LocalDate secondStepDate;

    private LocalDate thirdStepDate;

    private static final int SECOND_STEP = 4;
    private static final int THIRD_STEP = 7;

    public void changeFlower(Flower flower) {this.flower = flower;}
    public void recordStepIfNeeded(int wateringCount, LocalDate now) {

        if (wateringCount == SECOND_STEP && this.secondStepDate == null) {
            this.secondStepDate = now;
        } else if (wateringCount == THIRD_STEP && this.thirdStepDate == null) {
            this.thirdStepDate = now;
            this.isBloom = true;
            this.bloomAt = LocalDateTime.now();
        }
    }
}
