package umc.plantory.domain.terrarium.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.plantory.domain.flower.service.FlowerQueryUseCase;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.domain.terrarium.exception.TerrariumApiException;
import umc.plantory.domain.terrarium.repository.TerrariumJpaRepository;

@Service
@RequiredArgsConstructor
public class TerrariumQueryService implements TerrariumQueryUseCase{

    private final TerrariumJpaRepository terrariumJpaRepository;
    private final FlowerQueryUseCase flowerQueryUseCase;

    @Override
    public TerrariumResponseDto.TerrariumResponse getCurrentTerrariumData(Long memberId) {

        // memberId로 flowerId 조회
        Long flowerId = terrariumJpaRepository.findFlowerIdByMemberId(memberId)
                .orElseThrow(() -> new TerrariumApiException(TerrariumApiException.ErrorType.MEMBER_HAS_NO_TERRARIUM));
        
        String flowerImgUrl = flowerQueryUseCase.getFlowerImgUrl(flowerId);



        return null; // 임시 반환값
    }
}
