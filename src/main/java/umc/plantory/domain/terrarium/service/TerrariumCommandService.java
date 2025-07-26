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
import umc.plantory.domain.terrarium.exception.TerrariumApiException;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.MemberHandler;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TerrariumCommandService implements TerrariumCommandUseCase{
    private final MemberRepository memberRepository;
    private final TerrariumJpaRepository terrariumJpaRepository;
    private final WateringCanRepository wateringCanRepository;
    private final WateringEventJpaRepository wateringEventJpaRepository;

    /**
     * 회원이 테라리움에 물을 주고, 성장 단계 및 물뿌리기 이력을 처리
     * @param memberId  물을 주는 회원의 ID
     * @param terrariumId  물을 줄 테라리움의 ID
     */
    @Override
    public TerrariumResponseDto.WateringTerrariumResponse performTerrariumWatering(Long memberId, Long terrariumId) {
        Integer wateringCanCntById = memberRepository.findWateringCanCntById(memberId);
        int terrariumWateringCount = wateringEventJpaRepository.countByTerrariumId(terrariumId);
        int memberWateringCount = wateringCanCntById != null ? wateringCanCntById : 0;

        if (wateringCanCntById != null && wateringCanCntById > 0) {
            // 멤버 wateringCanCnt가 1 이상일 때만 실제 물주기 로직 수행
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
            member.decreaseWateringCanCount();
            memberRepository.save(member);

            List<WateringCan> availableWateringCans = wateringCanRepository.findAvailableByMemberIdOrderByDiaryCreatedAtAsc(memberId);
            if (availableWateringCans.isEmpty()) {
                log.info("물뿌리개 없음");
                return TerrariumResponseDto.WateringTerrariumResponse.builder()
                        .terrariumWateringCount(terrariumWateringCount)
                        .memberWateringCount(member.getWateringCanCnt())
                        .build();
            }
            WateringCan availableWateringCan = availableWateringCans.get(0);
            availableWateringCan.setIsUsed();
            wateringCanRepository.save(availableWateringCan);
            Terrarium currentTerrarium = terrariumJpaRepository.findById(terrariumId)
                    .orElseThrow(() -> new TerrariumApiException(TerrariumApiException.ErrorType.TERRARIUM_NOT_FOUND));

            // 물뿌리기 이력 저장
            WateringEvent wateringEvent = WateringEventConverter.toWateringEvent(availableWateringCan, currentTerrarium);
            wateringEventJpaRepository.save(wateringEvent);

            // 테라리움이 성장 단계 도달 시 성장 단계일 기록
            currentTerrarium.recordStepIfNeeded(terrariumWateringCount, LocalDateTime.now());
            terrariumJpaRepository.save(currentTerrarium);

            terrariumWateringCount = wateringEventJpaRepository.countByTerrariumId(currentTerrarium.getId());
            memberWateringCount = member.getWateringCanCnt();
        }

        return TerrariumResponseDto.WateringTerrariumResponse.builder()
                .terrariumWateringCount(terrariumWateringCount)
                .memberWateringCount(memberWateringCount)
                .build();
    }
}
