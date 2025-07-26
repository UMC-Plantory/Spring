package umc.plantory.domain.terrarium.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.flower.exception.FlowerApiException;
import umc.plantory.domain.flower.service.FlowerQueryUseCase;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.domain.terrarium.converter.TerrariumConverter;
import umc.plantory.domain.terrarium.entity.Terrarium;
import umc.plantory.domain.terrarium.exception.TerrariumApiException;
import umc.plantory.domain.terrarium.repository.TerrariumJpaRepository;
import umc.plantory.domain.wateringCan.repository.WateringEventJpaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TerrariumQueryService implements TerrariumQueryUseCase{

    private final TerrariumJpaRepository terrariumJpaRepository;
    private final FlowerQueryUseCase flowerQueryUseCase;
    private final MemberRepository memberRepository;
    private final WateringEventJpaRepository wateringEventJpaRepository;

    /**
     * 회원이 현재 키우고 있는(아직 개화하지 않은) 테라리움의 상세 데이터를 조회합니다.
     * @param memberId 회원 ID
     * @return 현재 키우고 있는 테라리움의 상세 정보 DTO
     * @throws TerrariumApiException 해당 회원의 테라리움이 없거나, 데이터 조회에 실패한 경우
     */
    @Override
    public TerrariumResponseDto.TerrariumResponse findCurrentTerrariumData(Long memberId) {
        Terrarium currentTerrarium = terrariumJpaRepository.findByMemberIdAndIsBloomFalse(memberId);
        if (currentTerrarium == null) {
            throw new TerrariumApiException(TerrariumApiException.ErrorType.MEMBER_HAS_NO_TERRARIUM, "해당 회원의 미개화 테라리움이 없습니다.");
        }
        if (currentTerrarium.getFlower() == null) {
            throw new TerrariumApiException(TerrariumApiException.ErrorType.FLOWER_NOT_FOUND_IN_TERRARIUM, "테라리움에 꽃 정보가 없습니다.");
        }

        String flowerImgUrl;
        try {
            flowerImgUrl = flowerQueryUseCase.getFlowerImgUrl(currentTerrarium.getFlower().getId());
        } catch (FlowerApiException e) {
            throw new TerrariumApiException(TerrariumApiException.ErrorType.FLOWER_NOT_FOUND_IN_TERRARIUM, "꽃 이미지 URL 조회에 실패했습니다.");
        }

        Integer wateringCanCnt;
        try {
            wateringCanCnt = memberRepository.findWateringCanCntById(memberId);
        } catch (Exception e) {
            throw new TerrariumApiException(TerrariumApiException.ErrorType.INVALID_MEMBER_ID, "회원의 물뿌리개 개수 조회에 실패했습니다.");
        }

        int wateringEventCnt;
        try {
            wateringEventCnt = wateringEventJpaRepository.countByTerrariumId(currentTerrarium.getId());
        } catch (Exception e) {
            throw new TerrariumApiException(TerrariumApiException.ErrorType.TERRARIUM_NOT_FOUND, "물주기 이벤트 개수 조회에 실패했습니다.");
        }

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
     * @param memberId 조회할 회원의 ID
     * @param year 조회할 연도 (예: 2025)
     * @param month 조회할 월 (1~12)
     * @return 지정한 회원이 해당 연도와 월에 개화가 완료된 테라리움 정보를 담은 CompletedTerrariumResponse 리스트
     */
    @Override
    public List<TerrariumResponseDto.CompletedTerrariumResponse> findCompletedTerrariumsByMonth(Long memberId, int year, int month) {
        return terrariumJpaRepository.findAllByMemberIdAndIsBloomTrueAndBloomAtYearAndMonth(memberId, year, month)
                .stream().map(terrarium -> new TerrariumResponseDto.CompletedTerrariumResponse(
                        terrarium.getId(), terrarium.getBloomAt(), terrarium.getFlower().getFlowerImgUrl(), terrarium.getFlower().getName()
                )).collect(Collectors.toList());
    }
}
