package umc.plantory.domain.wateringCan.repository;

import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.wateringCan.entity.WateringCan;

import java.util.Optional;

public interface WateringCanRepositoryCustom {
    Optional<WateringCan> findSelectedWateringCan(Member member);
}
