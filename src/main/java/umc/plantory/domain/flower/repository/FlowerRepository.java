package umc.plantory.domain.flower.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.flower.entity.Flower;
import umc.plantory.global.enums.Emotion;

public interface FlowerRepository extends JpaRepository<Flower, Long> {
    Flower findByEmotion(Emotion emotion);
}
