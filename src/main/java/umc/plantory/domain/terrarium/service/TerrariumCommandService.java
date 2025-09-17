package umc.plantory.domain.terrarium.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.flower.entity.Flower;
import umc.plantory.domain.flower.repository.FlowerRepository;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.terrarium.dto.TerrariumResponseDto;
import umc.plantory.domain.terrarium.converter.TerrariumConverter;
import umc.plantory.domain.terrarium.entity.Terrarium;
import umc.plantory.domain.terrarium.repository.TerrariumRepository;
import umc.plantory.domain.token.provider.JwtProvider;
import umc.plantory.domain.wateringCan.entity.WateringEvent;
import umc.plantory.domain.wateringCan.repository.WateringEventRepository;
import umc.plantory.domain.wateringCan.entity.WateringCan;
import umc.plantory.domain.wateringCan.repository.WateringCanRepository;
import umc.plantory.domain.wateringCan.converter.WateringEventConverter;
import umc.plantory.global.apiPayload.exception.handler.TerrariumHandler;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.MemberHandler;
import umc.plantory.global.apiPayload.exception.handler.WateringCanHandler;
import umc.plantory.global.enums.Emotion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

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
     */
    @Override
    @Transactional
    public TerrariumResponseDto.WateringTerrariumResponse performTerrariumWatering(String authorization, Long terrariumId) {
        int secondStepComplete = 3;
        int thirdStepComplete = 7;

        Long memberId = jwtProvider.getMemberIdAndValidateToken(authorization);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Terrarium terrarium = terrariumRepository.findById(terrariumId)
                .orElseThrow(() -> new TerrariumHandler(ErrorStatus.TERRARIUM_NOT_FOUND));

        // 현재 테라리움에 준 물의 수
        Integer currentWateringCnt = wateringEventRepository.countByTerrarium(terrarium);

        if (currentWateringCnt == thirdStepComplete) throw new TerrariumHandler(ErrorStatus.ALREADY_BLOOMED_TERRARIUM);

        // 사용할 물뿌리개 조회
        WateringCan selectedWateringCan = wateringCanRepository.findSelectedWateringCan(member)
                .orElseThrow(() -> new WateringCanHandler(ErrorStatus.NO_AVAILABLE_WATERING_CAN));

        // 물 사용 -> 해당 멤버의 물 Count - 1
        member.decreaseWateringCan();
        currentWateringCnt++;

        // 사용한 물뿌리개 저장
        WateringEvent newWateringEvent = saveNewWateringEvent(selectedWateringCan, terrarium);

        if (currentWateringCnt == secondStepComplete) {
            // 2번째 단계 시간 업데이트
            terrarium.updateSecondStepDate(LocalDate.now());

            return TerrariumConverter.toDefaultWateringTerrariumResponse(currentWateringCnt, member.getWateringCanCnt());
        } else if (currentWateringCnt == thirdStepComplete) {
            // 3번째 단계 진입 시 업데이트 필요한 데이터 업데이트
            terrarium.updateTerrariumDataForBloom(LocalDate.now(), LocalDateTime.now());
            member.increaseTotalBloomCnt();

            // 꽃 피는 부분 데이터 가져오는 로직 필요
            List<WateringEvent> wateringEventList = wateringEventRepository.findAllByTerrarium(terrarium);
            wateringEventList.add(newWateringEvent);
            // 7개인지 검증 (추후 변경 가능성 있음)
            if (wateringEventList.size() == thirdStepComplete) throw new TerrariumHandler(ErrorStatus.WATERING_CNT_INCORRECT);

            Map<Emotion, Integer> emotionList = new HashMap<>();
            for (WateringEvent wateringEvent : wateringEventList) {
                Emotion emotion = wateringEvent.getWateringCan().getEmotion();

                // 해당 감정이 있으면 값 + 1, 없으면 1 PUT
                emotionList.put(emotion, emotionList.getOrDefault(emotion, 0) + 1);
            }

            // Map에서 가장 큰 value 저장
            int maxCount = Collections.max(emotionList.values());
            // 해당 value 를 갖는 key(감정) List로 저장
            List<Emotion> mostFrequentEmotions = emotionList.entrySet().stream()
                    .filter(entry -> entry.getValue() == maxCount)
                    .map(Map.Entry::getKey)
                    .toList();

            // 랜덤 1개 선택
            Emotion randomEmotion = mostFrequentEmotions.get(ThreadLocalRandom.current().nextInt(mostFrequentEmotions.size()));

            // 해당 감정에 맞는 Flower 선택
            Flower flower = flowerRepository.findByEmotion(randomEmotion);

            // flower 업데이트
            terrarium.updateFlower(flower);

            // 새 테라리움 생성
            Flower defaultFlower = flowerRepository.findByEmotion(Emotion.DEFAULT);
            terrariumRepository.save(TerrariumConverter.toTerrarium(member, defaultFlower));

            return TerrariumConverter.toBloomWateringTerrariumResponse(currentWateringCnt, member.getWateringCanCnt(), emotionList, flower, member);
        } else return TerrariumConverter.toDefaultWateringTerrariumResponse(currentWateringCnt, member.getWateringCanCnt());
    }

    /**
     * 새로운 물뿌리개 생성 메서드
     */
    private WateringEvent saveNewWateringEvent (WateringCan selectedWateringCan, Terrarium terrarium) {
        return wateringEventRepository.save(WateringEventConverter.toWateringEvent(selectedWateringCan, terrarium));
    }
}
