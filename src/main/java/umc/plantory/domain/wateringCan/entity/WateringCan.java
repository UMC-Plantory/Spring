package umc.plantory.domain.wateringCan.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.global.enums.Emotion;

import java.time.LocalDate;

@Entity
@Table(name = "watering_can")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@DynamicInsert
@DynamicUpdate
public class WateringCan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "watering_can_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @Column(nullable = false)
    private LocalDate diaryDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Emotion emotion;

    @Column(nullable = false)
    private Boolean isUsed;

    public void setIsUsed() {
        if (isUsed == false) {
            isUsed = true;
        }
    }


}