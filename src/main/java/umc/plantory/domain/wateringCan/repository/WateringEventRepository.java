package umc.plantory.domain.wateringCan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.terrarium.entity.Terrarium;
import umc.plantory.domain.wateringCan.entity.WateringEvent;

public interface WateringEventRepository extends JpaRepository<WateringEvent, Long> {
    
    // 특정 테라리움의 물 준 횟수 계산 (JPA 메서드명으로 자동 생성)
    Integer countByTerrarium(Terrarium terrarium);
} 