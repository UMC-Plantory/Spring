package umc.plantory.domain.terrarium.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.plantory.domain.terrarium.entity.Terrarium;

import java.util.List;
import java.util.Optional;

public interface TerrariumRepository extends JpaRepository<Terrarium, Long> {

    Terrarium findByMemberIdAndIsBloomFalse(Long memberId);
    Optional<Terrarium> findByIdAndIsBloomTrue(Long terrariumId);
    @Query("SELECT t FROM Terrarium t " +
            "WHERE t.member.id = :memberId " +
            "AND t.isBloom = true " +
            "AND FUNCTION('YEAR', t.bloomAt) = :year " +
            "AND FUNCTION('MONTH', t.bloomAt) = :month")
    List<Terrarium> findAllByMemberIdAndIsBloomTrueAndBloomAtYearAndMonth(@Param("memberId") Long memberId, @Param("year") int year, @Param("month") int month);
}