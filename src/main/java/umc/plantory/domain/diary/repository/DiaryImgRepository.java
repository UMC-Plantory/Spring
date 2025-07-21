package umc.plantory.domain.diary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.diary.entity.DiaryImg;

import java.util.Optional;

public interface DiaryImgRepository extends JpaRepository<DiaryImg, Long> {
    Optional<DiaryImg> findByDiary(Diary diary);
}