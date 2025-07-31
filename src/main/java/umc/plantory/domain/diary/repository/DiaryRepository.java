package umc.plantory.domain.diary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.member.entity.Member;

import java.time.LocalDate;
import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findByMemberAndDiaryDateBetweenOrderByDiaryDate(Member member, LocalDate start, LocalDate end);
}