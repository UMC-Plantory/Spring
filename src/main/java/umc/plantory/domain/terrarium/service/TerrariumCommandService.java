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
import umc.plantory.domain.wateringCan.entity.WateringEvent;
import umc.plantory.domain.wateringCan.repository.WateringEventJpaRepository;
import umc.plantory.domain.wateringCan.entity.WateringCan;
import umc.plantory.domain.wateringCan.repository.WateringCanRepository;
import umc.plantory.domain.wateringCan.converter.WateringEventConverter;
import umc.plantory.global.apiPayload.exception.handler.FlowerHandler;
import umc.plantory.global.apiPayload.exception.handler.TerrariumHandler;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.MemberHandler;
import umc.plantory.global.apiPayload.exception.GeneralException;
import umc.plantory.global.enums.Emotion;

import java.time.LocalDateTime;
import java.util.Collections;
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
     * 회원의 테라리움을 물주는 작업을 수행합니다.
     *
     * @param authorization 인증용 JWT 토큰
     * @param terrariumId   물주기 대상인 테라리움의 고유 식별자
     */
    @Override
    @Transactional
    public TerrariumResponseDto.WateringTerrariumResponse performTerrariumWatering(String authorization, Long terrariumId) {

        // 회원 정보, 테라리움 정보 조회 및 검증
        // Authorization 헤더에서 토큰 추출
        String token = jwtProvider.resolveToken(authorization);
        if (token == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }

        // JWT 토큰 검증 및 멤버 ID 추출
        jwtProvider.validateToken(token);
        Long memberId = jwtProvider.getMemberId(token);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Terrarium terrarium = terrariumJpaRepository.findById(terrariumId)
                .orElseThrow(() -> new TerrariumHandler(ErrorStatus.TERRARIUM_NOT_FOUND));

        // 사용 가능한 물뿌리개 확인 및 WateringCan 개수 검증
        WateringCan wateringCan = findAvailableWateringCan(memberId);
        if (wateringCan == null || member.getWateringCanCnt() <= 0) {
            return buildCurrentStatusResponse(terrariumId, member.getWateringCanCnt(), terrarium.getFlower());
        }

        // 물주기 작업 수행
        try {
            WateringResult wateringResult = processWatering(member, terrarium, wateringCan);
            return buildWateringResponse(
                    wateringResult.getWateringCount(),
                    member.getWateringCanCnt(),
                    wateringResult.emotionCounts
                    ,terrarium.getFlower());
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.WATERING_PROCESS_FAILED);
        }
    }

    // 사용 가능한 물뿌리개 조회 (가장 오래된 일기 기준)
    private WateringCan findAvailableWateringCan(Long memberId) {
        List<WateringCan> available = wateringCanRepository.findUnusedByMemberIdOrderByDiaryCreatedAtAsc(memberId);
        return available.isEmpty() ? null : available.get(0);
    }

    // 물주기 처리 메서드
    private WateringResult processWatering(Member member, Terrarium terrarium, WateringCan wateringCan) {
        // WateringCan 개수 차감 후 저장
        member.decreaseWateringCan();
        memberRepository.save(member);

        // 새로운 WateringEvent 생성 및 저장 후 즉시 DB 반영
        WateringEvent wateringEvent = WateringEventConverter.toWateringEvent(wateringCan, terrarium);
        wateringEventJpaRepository.save(wateringEvent);
        wateringEventJpaRepository.flush();

        // 현재 테라리움에 대한 물주기 횟수 집계 및 단계 기록
        int wateringCount = wateringEventJpaRepository.countByTerrariumId(terrarium.getId());
        terrarium.recordStepIfNeeded(wateringCount, LocalDateTime.now());

        List<Object[]> emotionCounts = Collections.emptyList();
        // 7단계(꽃나무 단계) 도달 시 처리
        if (wateringCount >= 7) {
            emotionCounts = wateringEventJpaRepository.findEmotionCountsByTerrariumId(terrarium.getId());

            if (!emotionCounts.isEmpty()) {
                Emotion mostEmotion = (Emotion) emotionCounts.get(0)[0];

                Flower flowerForMostEmotion = flowerJpaRepository.findByEmotion(mostEmotion)
                        .orElseThrow(() -> new FlowerHandler(ErrorStatus.FLOWER_NOT_FOUND));

                // 테라리움의 꽃 변경
                terrarium.changeFlower(flowerForMostEmotion);
            }
            // 새로운 테라리움 생성
            createNewTerrarium(member);
        }
        terrariumJpaRepository.save(terrarium);
        terrariumJpaRepository.flush();

        return new WateringResult(wateringCount, emotionCounts);
    }

    // 현재 테라리움 상태 기반 응답 생성
    private TerrariumResponseDto.WateringTerrariumResponse buildCurrentStatusResponse(Long terrariumId,
                                                                                      int memberWateringCount,
                                                                                      Flower flower) {
        int terrariumWateringCount = wateringEventJpaRepository.countByTerrariumId(terrariumId);
        return buildWateringResponse(terrariumWateringCount, memberWateringCount, Collections.emptyList(), flower);
    }

    // 응답 DTO 빌드 메서드
    private TerrariumResponseDto.WateringTerrariumResponse buildWateringResponse(int terrariumWateringCount,
                                                                                 int memberWateringCount,
                                                                                 List<Object[]> emotionCounts,
                                                                                 Flower flower) {
        return TerrariumResponseDto.WateringTerrariumResponse.builder()
                .terrariumWateringCount(terrariumWateringCount)
                .memberWateringCount(memberWateringCount)
                .emotionCounts(emotionCounts)
                .flowerName(flower.getName())
                .flowerEmotion(flower.getEmotion())
                .build();
    }

    // 신규 테라리움 생성
    private void createNewTerrarium(Member member) {
        Flower baseflower = flowerJpaRepository.findByName("새싹")
                .orElseThrow(() -> new FlowerHandler(ErrorStatus.FLOWER_NOT_FOUND));

        Terrarium newTerrarium = TerrariumConverter.toTerrarium(member, baseflower);

        terrariumJpaRepository.save(newTerrarium);
        log.info("신규 테라리움 생성 - memberId: {}, terrariumId: {}", member.getId(), newTerrarium.getId());
    }

    // 결과 객체
    public class WateringResult {
        private final int wateringCount;
        private final List<Object[]> emotionCounts;

        public WateringResult(int wateringCount, List<Object[]> emotionCounts) {
            this.wateringCount = wateringCount;
            this.emotionCounts = emotionCounts;
        }

        public int getWateringCount() {
            return wateringCount;
        }
    }
}
