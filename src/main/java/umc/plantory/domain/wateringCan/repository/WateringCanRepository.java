package umc.plantory.domain.wateringCan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.terrarium.entity.Terrarium;
import umc.plantory.domain.wateringCan.entity.WateringCan;

import java.time.LocalDate;
import java.util.Optional;

public interface WateringCanRepository extends JpaRepository<WateringCan, Long> {
    Optional<WateringCan> findByDiary(Diary diary);
    boolean existsByDiaryDateAndMember(LocalDate diaryDate, Member member);
    
    // WateringEvent 테이블을 사용하여 특정 테라리움의 물 준 횟수 계산
    @Query("SELECT COUNT(we) FROM WateringEvent we WHERE we.terrarium = :terrarium")
    Integer countByTerrarium(@Param("terrarium") Terrarium terrarium);
}
