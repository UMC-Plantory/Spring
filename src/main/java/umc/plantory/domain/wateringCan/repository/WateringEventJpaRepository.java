package umc.plantory.domain.wateringCan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.plantory.domain.wateringCan.entity.WateringCan;
import umc.plantory.domain.wateringCan.entity.WateringEvent;

import java.util.List;

public interface WateringEventJpaRepository extends JpaRepository<WateringEvent, Long> {
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

}
