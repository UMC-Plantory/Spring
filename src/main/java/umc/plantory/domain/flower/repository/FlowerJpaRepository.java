package umc.plantory.domain.flower.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.plantory.domain.flower.entity.Flower;

import java.util.Optional;

public interface FlowerJpaRepository extends JpaRepository<Flower, Long> {
    
    /**
     * 꽃 ID로 꽃 이미지 URL 조회
     * 
     * @param flowerId 꽃 ID
     * @return 꽃 이미지 URL
     */
    @Query("select f.flowerImgUrl from Flower f where f.id = :flowerId")
    Optional<String> findFlowerImgUrlById(@Param("flowerId") Long flowerId);
}
