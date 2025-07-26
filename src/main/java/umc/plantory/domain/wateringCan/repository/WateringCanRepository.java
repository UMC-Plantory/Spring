package umc.plantory.domain.wateringCan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.wateringCan.entity.WateringCan;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface WateringCanRepository extends JpaRepository<WateringCan, Long> {
    @Query("SELECT wc FROM WateringCan wc " +
            "WHERE wc.diary.member.id = :memberId " +
            "AND wc.isUsed = false " +
            "ORDER BY wc.diary.createdAt ASC")
    List<WateringCan> findAvailableByMemberIdOrderByDiaryCreatedAtAsc(@Param("memberId") Long memberId);
}
