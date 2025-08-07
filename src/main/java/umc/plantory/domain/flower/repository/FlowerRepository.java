package umc.plantory.domain.flower.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.flower.entity.Flower;
import umc.plantory.global.enums.Emotion;

import java.util.Optional;

public interface FlowerRepository extends JpaRepository<Flower, Long> {
    Flower findByEmotion(Emotion emotion);
}
