package umc.plantory.domain.wateringCan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.wateringCan.entity.WateringEvent;

public interface WateringEventJpaRepository extends JpaRepository<WateringEvent, Long> {
    int countByTerrariumId(Long terrariumId);
}
