package umc.plantory.domain.diary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.diary.entity.Diary;

import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
}