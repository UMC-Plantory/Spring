package umc.plantory.domain.flower.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import umc.plantory.domain.flower.exception.FlowerApiException;
import umc.plantory.domain.flower.repository.FlowerJpaRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlowerQueryService implements FlowerQueryUseCase{

    private final FlowerJpaRepository flowerJpaRepository;

    @Override
    public String getFlowerImgUrl(Long flowerId) {
        String flowerImgUrl = flowerJpaRepository.findFlowerImgUrlById(flowerId)
                .orElseThrow(() -> new FlowerApiException(FlowerApiException.ErrorType.FLOWER_IMG_NOT_FOUND));
        System.out.println("flowerImgUrl : " + flowerImgUrl);

        return flowerImgUrl;
    }
}
