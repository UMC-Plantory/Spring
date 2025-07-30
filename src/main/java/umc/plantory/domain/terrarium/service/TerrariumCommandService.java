package umc.plantory.domain.terrarium.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.domain.terrarium.entity.Terrarium;
import umc.plantory.domain.terrarium.repository.TerrariumJpaRepository;
import umc.plantory.domain.wateringCan.repository.WateringEventJpaRepository;
import umc.plantory.domain.wateringCan.entity.WateringCan;
import umc.plantory.domain.wateringCan.repository.WateringCanRepository;
import umc.plantory.domain.wateringCan.converter.WateringEventConverter;
import umc.plantory.domain.wateringCan.entity.WateringEvent;
import umc.plantory.global.apiPayload.exception.handler.TerrariumHandler;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.MemberHandler;
import umc.plantory.global.apiPayload.exception.GeneralException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TerrariumCommandService implements TerrariumCommandUseCase {
    private final MemberRepository memberRepository;
    private final TerrariumJpaRepository terrariumJpaRepository;
    private final WateringCanRepository wateringCanRepository;
    private final WateringEventJpaRepository wateringEventJpaRepository;

    /**
     회원의 테라리움을 물주는 작업을 수행합니다.
      * @param memberId 물주기를 수행하는 회원의 고유 식별자
     * @param terrariumId 물주기 대상인 테라리움의 고유 식별자
     */
    @Override
    @Transactional
    public TerrariumResponseDto.WateringTerrariumResponse performTerrariumWatering(Long memberId, Long terrariumId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Terrarium terrarium = terrariumJpaRepository.findById(terrariumId)
                .orElseThrow(() -> new TerrariumHandler(ErrorStatus.TERRARIUM_NOT_FOUND));

        WateringCan wateringCan = findAvailableWateringCan(memberId);
        if (wateringCan == null) {
            return buildCurrentStatusResponse(terrariumId, member.getWateringCanCnt());
        }
        if (member.getWateringCanCnt() <= 0) {
            return buildCurrentStatusResponse(terrariumId, member.getWateringCanCnt());
        }

        try {
            int terrariumWateringCount = processWatering(member, terrarium, wateringCan);
            int memberWateringCount = member.getWateringCanCnt();
            return buildWateringResponse(terrariumWateringCount, memberWateringCount);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.WATERING_PROCESS_FAILED);
        }
    }

    private WateringCan findAvailableWateringCan(Long memberId) {
        List<WateringCan> available = wateringCanRepository.findUnusedByMemberIdOrderByDiaryCreatedAtAsc(memberId);
        return available.isEmpty() ? null : available.get(0);
    }

    private int processWatering(Member member, Terrarium terrarium, WateringCan wateringCan) {
        member.decreaseWateringCanCount();
        memberRepository.save(member);

        WateringEvent wateringEvent = WateringEventConverter.toWateringEvent(wateringCan, terrarium);
        wateringEventJpaRepository.save(wateringEvent);

        int wateringCount = wateringEventJpaRepository.countByTerrariumId(terrarium.getId());
        terrarium.recordStepIfNeeded(wateringCount, LocalDateTime.now());
        terrariumJpaRepository.save(terrarium);

        return wateringCount;
    }

    private TerrariumResponseDto.WateringTerrariumResponse buildCurrentStatusResponse(Long terrariumId, int memberWateringCount) {
        int terrariumWateringCount = wateringEventJpaRepository.countByTerrariumId(terrariumId);
        return buildWateringResponse(terrariumWateringCount, memberWateringCount);
    }

    private TerrariumResponseDto.WateringTerrariumResponse buildWateringResponse(int terrariumWateringCount, int memberWateringCount) {
        return TerrariumResponseDto.WateringTerrariumResponse.builder()
                .terrariumWateringCount(terrariumWateringCount)
                .memberWateringCount(memberWateringCount)
                .build();
    }
}
