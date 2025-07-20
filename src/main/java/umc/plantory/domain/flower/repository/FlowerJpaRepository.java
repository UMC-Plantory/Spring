package umc.plantory.domain.flower.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.flower.entity.Flower;

import java.util.Optional;

public interface FlowerJpaRepository extends JpaRepository<Flower, Long> {
    
    /**
     * 꽃 ID로 꽃 이미지 URL 조회
     * 
     * @param flowerId 꽃 ID
     * @return 꽃 이미지 URL
     */
    Optional<String> findFlowerImgUrlById(Long flowerId);
}
