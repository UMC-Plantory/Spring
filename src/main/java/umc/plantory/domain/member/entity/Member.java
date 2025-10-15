package umc.plantory.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import umc.plantory.global.baseEntity.BaseEntity;
import umc.plantory.global.enums.Gender;
import umc.plantory.global.enums.MemberRole;
import umc.plantory.global.enums.MemberStatus;
import umc.plantory.global.enums.Provider;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@DynamicInsert
@DynamicUpdate
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(length = 25, nullable = false)
    private String nickname;

    @Column(length = 100, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(length = 25, nullable = false)
    private String userCustomId;

    @Column(length = 255, nullable = false)
    private String profileImgUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false, updatable = false)
    private Provider provider;

    @Column(length = 255, nullable = false)
    private String providerId;

    private LocalDate birth;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer wateringCanCnt;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private MemberStatus status;

    private LocalDateTime inactiveAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 6, nullable = false)
    private MemberRole role;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer continuousRecordCnt;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer totalRecordCnt;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer avgSleepTime;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer totalBloomCnt;

    private LocalDate lastDiaryDate;

    @Column(nullable = false)
    @ColumnDefault("22")
    private Integer alarmTime;

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateUserCustomId(String userCustomId) {
        this.userCustomId = userCustomId;
    }

    public void updateBirth(LocalDate birth) {
        this.birth = birth;
    }

    public void updateGender(Gender gender) {
        this.gender = gender;
    }

    public void increaseWateringCan() {
        this.wateringCanCnt = this.wateringCanCnt + 1;
    }

    public void decreaseWateringCan() {this.wateringCanCnt -= 1;}

    public void increaseTotalBloomCnt() {this.totalBloomCnt += 1;}

    public void updateProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public void updateStatus(MemberStatus status) {
        this.status = status;
        if (status == MemberStatus.INACTIVE) {
            this.inactiveAt = LocalDateTime.now(); // 시간 업데이트
        }
    }

    public void increaseTotalRecordCnt() {
        this.totalRecordCnt += 1;
    }

    public void decreaseTotalRecordCnt() {
        this.totalRecordCnt -= 1;
    }

    public void increaseContinuousRecordCnt() {
        this.continuousRecordCnt = this.continuousRecordCnt + 1;
    }

    public void updateLastDiaryDate(LocalDate lastDiaryDate) {
        this.lastDiaryDate = lastDiaryDate;
    }

    public void updateAvgSleepTime(int minutes) {
        this.avgSleepTime = minutes;
    }

    public void updateAlarmTime(Integer alarmTime) { this.alarmTime = alarmTime; }
}
