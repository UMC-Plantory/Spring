package umc.plantory.domain.wateringCan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.wateringCan.entity.WateringCan;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

import java.time.LocalDate;
import java.util.Optional;

public interface WateringCanRepository extends JpaRepository<WateringCan, Long> {
    Optional<WateringCan> findByDiary(Diary diary);
    boolean existsByDiaryDateAndMember(LocalDate diaryDate, Member member);
    @Query("SELECT wc FROM WateringCan wc " +
            "WHERE wc.diary.member.id = :memberId " +
            "AND wc.isUsed = false " +
            "ORDER BY wc.diary.createdAt ASC")
    List<WateringCan> findAvailableByMemberIdOrderByDiaryCreatedAtAsc(@Param("memberId") Long memberId);
}
