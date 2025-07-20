package umc.plantory.domain.wateringCan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.wateringCan.entity.WateringCan;

public interface WateringCanRepository extends JpaRepository<WateringCan, Long> {
}
