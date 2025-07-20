package umc.plantory.domain.terrarium.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.plantory.domain.flower.service.FlowerQueryUseCase;
import umc.plantory.domain.member.repository.MemberRepository;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.domain.terrarium.converter.TerrariumConverter;
import umc.plantory.domain.terrarium.entity.Terrarium;
import umc.plantory.domain.terrarium.exception.TerrariumApiException;
import umc.plantory.domain.terrarium.repository.TerrariumJpaRepository;
import umc.plantory.domain.wateringCan.repository.WateringEventJpaRepository;

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
     * <ul>
     *     <li>테라리움 엔티티를 memberId와 isBloom=false 조건으로 조회</li>
     *     <li>해당 테라리움의 꽃 이미지 URL 조회</li>
     *     <li>회원의 전체 물뿌리개 개수 조회</li>
     *     <li>해당 테라리움에 대한 물주기 이벤트 개수 조회</li>
     *     <li>위 정보를 종합하여 TerrariumResponseDto.TerrariumResponse로 변환</li>
     * </ul>
     * @param memberId 회원 ID
     * @return 현재 키우고 있는 테라리움의 상세 정보 DTO
     * @throws TerrariumApiException 해당 회원의 테라리움이 없거나, 데이터 조회에 실패한 경우
     */
    @Override
    public TerrariumResponseDto.TerrariumResponse getCurrentTerrariumData(Long memberId) {
        Terrarium currentTerrarium = terrariumJpaRepository.findByMemberIdAndIsBloomFalse(memberId);

        return TerrariumConverter.toTerrariumResponse(
                flowerQueryUseCase.getFlowerImgUrl(currentTerrarium.getFlower().getId()),
                memberRepository.findWateringCanCntById(memberId),
                wateringEventJpaRepository.countByTerrariumId(currentTerrarium.getId()));
    }
}
