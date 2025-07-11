package umc.plantory.domain.wateringCan.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import umc.plantory.domain.terrarium.entity.Terrarium;
import umc.plantory.global.enums.Emotion;

import java.time.LocalDate;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@DynamicInsert
@DynamicUpdate
public class WateringEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "watering_event_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terrarium_id")
    private Terrarium terrarium;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "watering_can_id")
    private WateringCan wateringCan;

    @Column(nullable = false)
    private LocalDate diaryDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Emotion emotion;

}
