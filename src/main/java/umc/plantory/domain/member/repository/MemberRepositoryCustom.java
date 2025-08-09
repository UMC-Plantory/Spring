package umc.plantory.domain.member.repository;

import java.time.LocalDate;

public interface MemberRepositoryCustom {
    long resetStreak(LocalDate yesterday);
}