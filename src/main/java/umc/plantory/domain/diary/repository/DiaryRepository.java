package umc.plantory.domain.diary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.global.enums.DiaryStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    Optional<Diary> findByMemberIdAndDiaryDateAndStatusIn(Long memberId, LocalDate date, List<DiaryStatus> statusList);
    boolean existsByMemberIdAndDiaryDateAndStatus(Long memberId, LocalDate diaryDate, DiaryStatus status);
    List<Diary> findByMemberAndStatusInAndDiaryDateBetween(Member member, List<DiaryStatus> diaryStatuses, LocalDate start, LocalDate end);
    
    // 홈화면용 메서드들
    Optional<Diary> findByMemberAndDiaryDateAndStatus(Member member, LocalDate diaryDate, DiaryStatus status);
    
    @Query("SELECT d FROM Diary d WHERE d.member = :member AND YEAR(d.diaryDate) = :year AND MONTH(d.diaryDate) = :month AND d.status = :status")
    List<Diary> findByMemberAndYearAndMonthAndStatus(@Param("member") Member member, @Param("year") int year, @Param("month") int month, @Param("status") DiaryStatus status);
    
    // 연속 기록 횟수 계산
    @Query("SELECT COUNT(d) FROM Diary d WHERE d.member = :member AND d.status = :status AND d.diaryDate >= :startDate AND d.diaryDate <= :endDate")
    Integer countContinuousRecords(@Param("member") Member member, @Param("status") DiaryStatus status, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // 특정 날짜에 일기가 작성되었는지 확인
    @Query("SELECT COUNT(d) > 0 FROM Diary d WHERE d.member = :member AND d.diaryDate = :date AND d.status = :status")
    Boolean existsByMemberAndDiaryDateAndStatus(@Param("member") Member member, @Param("date") LocalDate date, @Param("status") DiaryStatus status);
}