package umc.plantory.domain.wateringCan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.wateringCan.entity.WateringCan;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WateringCanRepository extends JpaRepository<WateringCan, Long> {
    Optional<WateringCan> findByDiary(Diary diary);
    boolean existsByDiaryDateAndMember(LocalDate diaryDate, Member member);
    @Query("SELECT wc FROM WateringCan wc " +
            "WHERE wc.diary.member.id = :memberId " +
            "AND NOT EXISTS (" +
            "SELECT we FROM WateringEvent we " +
            "WHERE we.wateringCan = wc) " +
            "ORDER BY wc.diary.createdAt ASC")
    List<WateringCan> findUnusedByMemberIdOrderByDiaryCreatedAtAsc(@Param("memberId") Long memberId);
}
