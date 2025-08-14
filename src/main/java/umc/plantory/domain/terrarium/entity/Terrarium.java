package umc.plantory.domain.terrarium.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import umc.plantory.domain.flower.entity.Flower;
import umc.plantory.domain.member.entity.Member;

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

    // 2단계 진입 시 사용
    public void updateSecondStepDate(LocalDate secondStepDate) {
        this.secondStepDate = secondStepDate;
    }

    // 3단계 진입 시 사용
    public void updateTerrariumDataForBloom(LocalDate thirdStepDate, LocalDateTime bloomAt) {
        this.thirdStepDate = thirdStepDate;
        this.bloomAt = bloomAt;
        this.isBloom = true;
    }
}
