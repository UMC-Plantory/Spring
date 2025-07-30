package umc.plantory.domain.terrarium.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.domain.terrarium.converter.TerrariumConverter;
import umc.plantory.domain.terrarium.entity.Terrarium;
import umc.plantory.domain.token.provider.JwtProvider;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.handler.TerrariumHandler;
import umc.plantory.domain.terrarium.repository.TerrariumJpaRepository;
import umc.plantory.domain.wateringCan.repository.WateringEventJpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import umc.plantory.global.enums.Emotion;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TerrariumQueryService implements TerrariumQueryUseCase{

    private final TerrariumJpaRepository terrariumJpaRepository;
    private final MemberRepository memberRepository;
    private final WateringEventJpaRepository wateringEventJpaRepository;
    private final JwtProvider jwtProvider;

    /**
     * 회원이 현재 키우고 있는(아직 개화하지 않은) 테라리움의 상세 데이터를 조회합니다.
     * @param authorization 인증용 JWT 토큰
     * @return 현재 키우고 있는 테라리움의 상세 정보 DTO
     * @throws TerrariumHandler 해당 회원의 테라리움이 없거나, 데이터 조회에 실패한 경우
     */
    @Override
    public TerrariumResponseDto.TerrariumResponse findCurrentTerrariumData(String authorization) {
        Long memberId = jwtProvider.getMemberId(authorization);
        Terrarium currentTerrarium = terrariumJpaRepository.findByMemberIdAndIsBloomFalse(memberId);
        if (currentTerrarium == null) {
            throw new TerrariumHandler(ErrorStatus.MEMBER_HAS_NO_TERRARIUM);
        }
        if (currentTerrarium.getFlower() == null) {
            throw new TerrariumHandler(ErrorStatus.FLOWER_NOT_FOUND_IN_TERRARIUM);
        }


        String flowerImgUrl = currentTerrarium.getFlower().getFlowerImgUrl();
        if (flowerImgUrl == null) {
            throw new TerrariumHandler(ErrorStatus.FLOWER_IMG_NOT_FOUND_IN_TERRARIUM);
        }

        Integer wateringCanCnt = memberRepository.findWateringCanCntById(memberId);

        int wateringEventCnt = wateringEventJpaRepository.countByTerrariumId(currentTerrarium.getId());

        return TerrariumConverter.toTerrariumResponse(
                currentTerrarium.getId(),
                flowerImgUrl,
                wateringCanCnt,
                wateringEventCnt
        );
    }

    /**
     * 회원 ID와 연도, 월을 기준으로 개화가 완료된 테라리움 목록을 조회합니다.
     *
     * @param authorization 인증용 JWT 토큰
     * @param year 조회할 연도 (예: 2025)
     * @param month 조회할 월 (1~12)
     * @return 지정한 회원이 해당 연도와 월에 개화가 완료된 테라리움 정보를 담은 CompletedTerrariumResponse 리스트
     */
    @Override
    public List<TerrariumResponseDto.CompletedTerrariumResponse> findCompletedTerrariumsByMonth(String authorization, int year, int month) {
        Long memberId = jwtProvider.getMemberId(authorization);
        return terrariumJpaRepository.findAllByMemberIdAndIsBloomTrueAndBloomAtYearAndMonth(memberId, year, month)
                .stream().map(terrarium -> new TerrariumResponseDto.CompletedTerrariumResponse(
                        terrarium.getId(),
                        terrarium.getBloomAt(),
                        terrarium.getFlower().getFlowerImgUrl(),
                        terrarium.getFlower().getName()
                )).collect(Collectors.toList());
    }

    @Override
    public TerrariumResponseDto.CompletedTerrariumDetatilResponse findCompletedTerrariumDetail(String authorization, Long terrariumId) {
        Long memberId = jwtProvider.getMemberId(authorization);
        // 테라리움 id로 Diary 조회
        List<Diary> diariesByTerrariumId = terrariumJpaRepository.findDiariesByTerrariumId(terrariumId);
        List<LocalDate> usedDiaries = diariesByTerrariumId.stream()
                .map(diary -> diary.getDiaryDate())
                .collect(Collectors.toList());

        Terrarium terrarium = terrariumJpaRepository.findById(terrariumId)
                .orElseThrow(() -> new TerrariumHandler(ErrorStatus.TERRARIUM_NOT_FOUND));
        
        // 가장 많이 나온 emotion 찾기
        String mostEmotion = findMostFrequentEmotion(diariesByTerrariumId);

        return TerrariumResponseDto.CompletedTerrariumDetatilResponse.builder()
                .startAt(terrarium.getStartAt())
                .bloomAt(terrarium.getBloomAt())
                .firstStepDate(terrarium.getFirstStepDate())
                .secondStepDate(terrarium.getSecondStepDate())
                .thirdStepDate(terrarium.getThirdStepDate())
                .mostEmotion(mostEmotion)
                .build();
    }

    /**
     * diary 리스트에서 가장 많이 나온 emotion을 찾습니다.
     * @param diaries emotion을 확인할 diary 리스트
     * @return 가장 많이 나온 emotion의 이름, 없으면 "UNKNOWN"
     */
    private String findMostFrequentEmotion(List<Diary> diaries) {
        Map<Emotion, Long> emotionCounts = new HashMap<>();
        
        // emotion별 개수 세기
        for (Diary diary : diaries) {
            Emotion emotion = diary.getEmotion();
            if (emotion != null) {
                emotionCounts.put(emotion, emotionCounts.getOrDefault(emotion, 0L) + 1);
            }
        }
        
        // 가장 많이 나온 emotion 찾기
        Emotion mostFrequentEmotion = null;
        long maxCount = 0;
        
        for (Map.Entry<Emotion, Long> entry : emotionCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFrequentEmotion = entry.getKey();
            }
        }
        
        return mostFrequentEmotion != null ? mostFrequentEmotion.name() : "UNKNOWN";
    }
}
