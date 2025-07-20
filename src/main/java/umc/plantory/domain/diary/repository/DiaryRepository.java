package umc.plantory.domain.diary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.diary.entity.Diary;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
}