package umc.plantory.domain.diary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.global.enums.DiaryStatus;

import java.time.LocalDate;
import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<Diary> findByMemberAndStatusInAndDiaryDateBetween(Member member, List<DiaryStatus> diaryStatuses, LocalDate start, LocalDate end);
}