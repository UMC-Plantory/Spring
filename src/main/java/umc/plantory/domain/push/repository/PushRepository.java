package umc.plantory.domain.push.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.push.dto.PushDataDTO;
import umc.plantory.domain.push.entity.PushData;

import java.util.List;
import java.util.Optional;

public interface PushRepository extends JpaRepository<PushData, Long> {
    Optional<PushData> findByMember(Member member);
    @Query("SELECT p.fcmToken FROM PushData p JOIN p.member m WHERE m.alarmTime = :alarmTime AND m.status = 'ACTIVE' AND p.tokenStatus = 'VALID'")
    List<String> findFcmTokenByAlarmTime(@Param("alarmTime") Integer alarmTime);
    @Query("SELECT p.fcmToken, " +
            "  CASE " +
            "    WHEN FUNCTION('DATEDIFF', CURRENT_DATE, m.lastDiaryDate) = 3 THEN 'three' " +
            "    WHEN FUNCTION('DATEDIFF', CURRENT_DATE, m.lastDiaryDate) = 5 THEN 'five' " +
            "    WHEN FUNCTION('DATEDIFF', CURRENT_DATE, m.lastDiaryDate) = 7 THEN 'seven' " +
            "    WHEN FUNCTION('DATEDIFF', CURRENT_DATE, m.lastDiaryDate) = 14 THEN 'fourteen' " +
            "    WHEN FUNCTION('DATEDIFF', CURRENT_DATE, m.lastDiaryDate) = 30 THEN 'thirty' " +
            "    ELSE '기타' " +
            "  END AS dateDifference " +
            "FROM PushData p " +
            "JOIN p.member m " +
            "WHERE FUNCTION('DATEDIFF', CURRENT_DATE, m.lastDiaryDate) IN (3, 5, 7, 14, 30) " +
            "AND m.status = 'ACTIVE' AND p.tokenStatus = 'VALID'")
    List<PushDataDTO.FcmTokenDateDiffDTO> findFcmTokenByLastDiaryDate();
    void deleteAllByMember(Member member);

}
