package umc.plantory.domain.flower.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.flower.entity.Flower;
import umc.plantory.global.enums.Emotion;

import java.util.Optional;

public interface FlowerJpaRepository extends JpaRepository<Flower, Long> {
    Optional<Flower> findByName(String name);
    Optional<Flower> findByEmotion(Emotion emotion);
    Optional<String> findFlowerImgUrlById(Long flowerId);
}
