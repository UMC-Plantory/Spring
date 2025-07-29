package umc.plantory.domain.diary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.diary.entity.DiaryImg;

public interface DiaryImgRepository extends JpaRepository<DiaryImg, Long> {
}