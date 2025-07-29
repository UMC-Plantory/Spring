package umc.plantory.domain.diary.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.global.baseEntity.BaseEntity;
import umc.plantory.global.enums.DiaryStatus;
import umc.plantory.global.enums.Emotion;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@DynamicInsert
@DynamicUpdate
public class Diary extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Emotion emotion;

    @Column(length = 30)
    private String title;

    @Column(length = 300)
    private String content;

    private LocalDateTime sleepEndTime;

    private LocalDateTime sleepStartTime;

    private LocalDateTime deletedAt;

    @Column(nullable = false, updatable = false)
    private LocalDate diaryDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private DiaryStatus status;

    public void update(Emotion emotion,
                       String content,
                       LocalDateTime sleepStartTime,
                       LocalDateTime sleepEndTime,
                       DiaryStatus status) {

        this.emotion = emotion;
        this.content = content;
        this.sleepStartTime = sleepStartTime;
        this.sleepEndTime = sleepEndTime;
        this.status = status;
    }

    public void updateStatus(DiaryStatus status) {
        this.status = status;
    }

    public void updateDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
