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
    @Query("SELECT we.wateringCan FROM WateringEvent we WHERE we.terrarium.id = :terrariumId")
    List<WateringCan> findWateringCanListByTerrariumId(@Param("terrariumId") Long terrariumId);
    Integer countByTerrarium(Terrarium terrarium);
    List<WateringEvent> findAllByTerrarium(Terrarium terrarium);
    List<WateringEvent> findAllByTerrariumIn(List<Terrarium> terrariumList);
}
