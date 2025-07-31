package umc.plantory.domain.diary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.global.enums.DiaryStatus;

import java.time.LocalDate;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    Optional<Diary> findByMemberIdAndDiaryDateAndStatus(Long memberId, LocalDate diaryDate, DiaryStatus status);
    boolean existsByMemberIdAndDiaryDateAndStatus(Long memberId, LocalDate diaryDate, DiaryStatus status);
}