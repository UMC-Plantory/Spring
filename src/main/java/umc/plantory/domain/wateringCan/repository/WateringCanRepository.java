package umc.plantory.domain.wateringCan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.wateringCan.entity.WateringCan;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WateringCanRepository extends JpaRepository<WateringCan, Long>, WateringCanRepositoryCustom {
    Optional<WateringCan> findByDiary(Diary diary);
    boolean existsByDiaryDateAndMember(LocalDate diaryDate, Member member);
    List<WateringCan> findByDiaryIn(List<Diary> diaryList);
}
