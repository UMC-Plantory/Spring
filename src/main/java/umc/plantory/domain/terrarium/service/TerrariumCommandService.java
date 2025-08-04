package umc.plantory.domain.terrarium.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.flower.entity.Flower;
import umc.plantory.domain.flower.repository.FlowerRepository;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.domain.terrarium.converter.TerrariumConverter;
import umc.plantory.domain.terrarium.entity.Terrarium;
import umc.plantory.domain.terrarium.repository.TerrariumRepository;
import umc.plantory.domain.token.provider.JwtProvider;
import umc.plantory.domain.wateringCan.entity.WateringEvent;
import umc.plantory.domain.wateringCan.repository.WateringEventRepository;
import umc.plantory.domain.wateringCan.entity.WateringCan;
import umc.plantory.domain.wateringCan.repository.WateringCanRepository;
import umc.plantory.domain.wateringCan.converter.WateringEventConverter;
import umc.plantory.global.apiPayload.exception.handler.FlowerHandler;
import umc.plantory.global.apiPayload.exception.handler.TerrariumHandler;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.MemberHandler;
import umc.plantory.global.apiPayload.exception.GeneralException;
import umc.plantory.global.enums.Emotion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TerrariumCommandService implements TerrariumCommandUseCase {

    private final MemberRepository memberRepository;
    private final TerrariumRepository terrariumRepository;
    private final WateringCanRepository wateringCanRepository;
    private final WateringEventRepository wateringEventRepository;
    private final FlowerRepository flowerRepository;
    private final JwtProvider jwtProvider;

    /**
     * 회원의 테라리움을 물주는 작업을 수행합니다.
     *
     * @param authorization 인증용 JWT 토큰
     * @param terrariumId   현재 키우고 있는 중인 테라리움 id
     */
    @Override
    @Transactional
    public TerrariumResponseDto.WateringTerrariumResponse performTerrariumWatering(String authorization, Long terrariumId) {

        String token = jwtProvider.resolveToken(authorization);
        if (token == null) {
            throw new MemberHandler(ErrorStatus._UNAUTHORIZED);
        }
        jwtProvider.validateToken(token);
        Long memberId = jwtProvider.getMemberId(token);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Terrarium terrarium = terrariumRepository.findById(terrariumId)
                .orElseThrow(() -> new TerrariumHandler(ErrorStatus.TERRARIUM_NOT_FOUND));
        // 사용 가능한 물뿌리개 확인 및 WateringCan 개수 검증
        WateringCan wateringCan = findAvailableWateringCan(memberId);
        // 물주기 작업 수행
        try {
            WateringResult wateringResult = processWatering(member, terrarium, wateringCan);
            return TerrariumConverter.toWateringTerrariumResponse(
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
        List<WateringCan> availableWateringCanList = wateringCanRepository.findUnusedByMemberIdOrderByDiaryCreatedAtAsc(memberId);
        WateringCan wateringCan = availableWateringCanList.get(0);
        if (wateringCan == null) {
            throw new TerrariumHandler(ErrorStatus.NO_AVAILABLE_WATERING_CAN);
        }
        return wateringCan;
    }

    // 물주기 처리 메서드
    private WateringResult processWatering(Member member, Terrarium terrarium, WateringCan wateringCan) {
        // WateringCan 개수 차감 후 저장
        member.decreaseWateringCan();

        // 새로운 WateringEvent 생성 및 저장 후 즉시 DB 반영
        WateringEvent wateringEvent = WateringEventConverter.toWateringEvent(wateringCan, terrarium);
        wateringEventRepository.save(wateringEvent);
        wateringEventRepository.flush();

        // 현재 테라리움에 대한 물주기 횟수 집계 및 단계 기록
        int TerrariumwateringCount = wateringEventRepository.countByTerrariumId(terrarium.getId());
        terrarium.recordStepIfNeeded(TerrariumwateringCount, LocalDate.now());

        List<Object[]> emotionCounts = Collections.emptyList();
        // 7단계(꽃나무 단계) 도달 시 응답 형식이 달라야 하기 때문에 emotionCounts 생성
        if (TerrariumwateringCount >= 7) {
            emotionCounts = wateringEventRepository.findEmotionCountsByTerrariumId(terrarium.getId());

            if (!emotionCounts.isEmpty()) {
                Emotion mostEmotion = (Emotion) emotionCounts.get(0)[0];

                Flower flowerForMostEmotion = flowerRepository.findByEmotion(mostEmotion);

                // 테라리움의 꽃 변경
                terrarium.changeFlower(flowerForMostEmotion);
            }
            // 새로운 테라리움 생성
            member.increaseTotalBloomCnt();
            createNewTerrarium(member);
        }

        memberRepository.save(member);
        terrariumRepository.save(terrarium);
        terrariumRepository.flush();

        return new WateringResult(TerrariumwateringCount, emotionCounts);
    }

    // 신규 테라리움 생성
    private void createNewTerrarium(Member member) {
        Flower baseflower = flowerRepository.findByEmotion(Emotion.DEFAULT);

        Terrarium terrariumCurrentIsBloomFalse = terrariumRepository.findByMemberIdAndIsBloomFalse(member.getId());

        if (!(terrariumCurrentIsBloomFalse == null)) {
            throw new TerrariumHandler(ErrorStatus.TERRARIUM_ALREADY_IN_PROGRESS);
        } else {
            Terrarium newTerrarium = TerrariumConverter.toTerrarium(member, baseflower);
            terrariumRepository.save(newTerrarium);
            log.info("신규 테라리움 생성 - memberId: {}, terrariumId: {}", member.getId(), newTerrarium.getId());
        }
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
