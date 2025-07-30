package umc.plantory.domain.terrarium.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.flower.entity.Flower;
import umc.plantory.domain.flower.repository.FlowerJpaRepository;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.domain.terrarium.converter.TerrariumConverter;
import umc.plantory.domain.terrarium.entity.Terrarium;
import umc.plantory.domain.terrarium.repository.TerrariumJpaRepository;
import umc.plantory.domain.token.provider.JwtProvider;
import umc.plantory.domain.wateringCan.repository.WateringEventJpaRepository;
import umc.plantory.domain.wateringCan.entity.WateringCan;
import umc.plantory.domain.wateringCan.repository.WateringCanRepository;
import umc.plantory.domain.wateringCan.converter.WateringEventConverter;
import umc.plantory.domain.wateringCan.entity.WateringEvent;
import umc.plantory.global.apiPayload.exception.handler.FlowerHandler;
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
    private final FlowerJpaRepository flowerJpaRepository;
    private final JwtProvider jwtProvider;

    /**
     회원의 테라리움을 물주는 작업을 수행합니다.
      * @param authorization 인증용 JWT 토큰
     * @param terrariumId 물주기 대상인 테라리움의 고유 식별자
     */
    @Override
    @Transactional
    public TerrariumResponseDto.WateringTerrariumResponse performTerrariumWatering(String authorization, Long terrariumId) {
        Long memberId = jwtProvider.getMemberId(authorization);

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
            WateringResult wateringResult = processWatering(member, terrarium, wateringCan);
            int terrariumWateringCount = wateringResult.wateringCount;
            terrarium = wateringResult.terrarium;
            int memberWateringCount = member.getWateringCanCnt();
            return buildWateringResponse(terrarium.getId(), terrariumWateringCount, memberWateringCount);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.WATERING_PROCESS_FAILED);
        }
    }

    private WateringCan findAvailableWateringCan(Long memberId) {
        List<WateringCan> available = wateringCanRepository.findUnusedByMemberIdOrderByDiaryCreatedAtAsc(memberId);
        return available.isEmpty() ? null : available.get(0);
    }

    private WateringResult processWatering(Member member, Terrarium terrarium, WateringCan wateringCan) {
        member.decreaseWateringCan();
        memberRepository.save(member);

        WateringEvent wateringEvent = WateringEventConverter.toWateringEvent(wateringCan, terrarium);
        wateringEventJpaRepository.save(wateringEvent);

        int wateringCount = wateringEventJpaRepository.countByTerrariumId(terrarium.getId());
        terrarium.recordStepIfNeeded(wateringCount, LocalDateTime.now());
        terrariumJpaRepository.save(terrarium);

        if (wateringCount >= 7) {
            terrarium = createNewTerrarium(member);
        }

        return new WateringResult(wateringCount, terrarium);
    }

    private TerrariumResponseDto.WateringTerrariumResponse buildCurrentStatusResponse(Long terrariumId, int memberWateringCount) {
        int terrariumWateringCount = wateringEventJpaRepository.countByTerrariumId(terrariumId);
        return buildWateringResponse(terrariumId, terrariumWateringCount, memberWateringCount);
    }

    private TerrariumResponseDto.WateringTerrariumResponse buildWateringResponse(Long terrariumId, int terrariumWateringCount, int memberWateringCount) {
        return TerrariumResponseDto.WateringTerrariumResponse.builder()
                .terrariumId(terrariumId)
                .terrariumWateringCount(terrariumWateringCount)
                .memberWateringCount(memberWateringCount)
                .build();
    }

    private Terrarium createNewTerrarium(Member member) {
        Flower baseflower = flowerJpaRepository.findByName("새싹")
                .orElseThrow(() -> new FlowerHandler(ErrorStatus.FLOWER_NOT_FOUND));

        Terrarium newTerrarium = TerrariumConverter.toTerrarium(member, baseflower);

        terrariumJpaRepository.save(newTerrarium);

        log.info("신규 테라리움 생성 - memberId: {}, terrariumId: {}", member.getId(), newTerrarium.getId());
        return newTerrarium;
    }

    public class WateringResult {
        private final int wateringCount;
        private final Terrarium terrarium;

        public WateringResult(int wateringCount, Terrarium terrarium) {
            this.wateringCount = wateringCount;
            this.terrarium = terrarium;
        }

        public int getWateringCount() {
            return wateringCount;
        }

        public Terrarium getTerrarium() {
            return terrarium;
        }
    }
}
