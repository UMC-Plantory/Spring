package umc.plantory.domain.flower.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.plantory.domain.flower.exception.FlowerApiException;
import umc.plantory.domain.flower.repository.FlowerJpaRepository;

@Service
@RequiredArgsConstructor
public class FlowerQueryService implements FlowerQueryUseCase{

    private final FlowerJpaRepository flowerJpaRepository;

    @Override
    public String getFlowerImgUrl(Long flowerId) {
        return flowerJpaRepository.findFlowerImgUrlById(flowerId)
                .orElseThrow(() -> new FlowerApiException(FlowerApiException.ErrorType.FLOWER_IMG_NOT_FOUND));
    }
}
