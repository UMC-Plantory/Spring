package umc.plantory.domain.diary.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.diary.dto.DiaryProjectionDTO;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.global.enums.DiaryStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long>, DiaryRepositoryCustom {
    Optional<Diary> findByMemberIdAndDiaryDateAndStatusIn(Long memberId, LocalDate date, List<DiaryStatus> statusList);
    boolean existsByMemberIdAndDiaryDateAndStatus(Long memberId, LocalDate diaryDate, DiaryStatus status);
    List<Diary> findByMemberAndStatusInAndDiaryDateBetween(Member member, List<DiaryStatus> diaryStatuses, LocalDate start, LocalDate end);
    List<Diary> findAllByMemberIdAndStatus(Long memberId, DiaryStatus status, Sort sort);
    List<Diary> findByStatusAndDeletedAtBefore(DiaryStatus status, LocalDateTime threshold);
    List<DiaryProjectionDTO.SleepIntervalDTO> findByMemberInAndStatusInAndDiaryDateBetween(
            List<Member> members, List<DiaryStatus> statuses, LocalDate start, LocalDate end
    );
}