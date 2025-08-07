package umc.plantory.domain.wateringCan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.terrarium.entity.Terrarium;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.plantory.domain.wateringCan.entity.WateringCan;
import umc.plantory.domain.wateringCan.entity.WateringEvent;

import java.util.List;

public interface WateringEventRepository extends JpaRepository<WateringEvent, Long> {
    int countByTerrariumId(Long terrariumId);
    @Query("SELECT wc.emotion, COUNT(wc.emotion) as cnt " +
            "FROM WateringEvent we " +
            "JOIN we.wateringCan wc " +
            "WHERE we.terrarium.id = :terrariumId " +
            "GROUP BY wc.emotion " +
            "ORDER BY cnt DESC")
    List<Object[]> findEmotionCountsByTerrariumId(@Param("terrariumId") Long terrariumId);
    @Query("SELECT we.wateringCan FROM WateringEvent we WHERE we.terrarium.id = :terrariumId")
    List<WateringCan> findWateringCanListByTerrariumId(@Param("terrariumId") Long terrariumId);
  
    // 특정 테라리움의 물 준 횟수 계산 (JPA 메서드명으로 자동 생성)
    Integer countByTerrarium(Terrarium terrarium);

}
