package umc.plantory.domain.terrarium.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.domain.terrarium.service.TerrariumCommandUseCase;
import umc.plantory.global.apiPayload.ApiResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/plantory/terrarium")
public class TerrariumCommandController implements TerrariumCommandApi{

    private final TerrariumCommandUseCase terrariumCommandUseCase;

    /**
     * 테라리움에 물을 주는 요청을 처리합니다.
     *
     * @param terrariumId 물을 줄 테라리움의 ID
     * @param memberId 요청 회원의 ID
     * @return 물주기 결과 및 테라리움 상태 정보
     */
    @Override
    @PostMapping("/{terrarium-id}/water")
    public ResponseEntity<ApiResponse<TerrariumResponseDto.WateringTerrariumResponse>> waterTerrarium
            (@PathVariable("terrarium-id") Long terrariumId,
             Long memberId) {
        TerrariumResponseDto.WateringTerrariumResponse wateringTerrariumResponse = terrariumCommandUseCase.performTerrariumWatering(memberId, terrariumId);
        return ResponseEntity.ok(ApiResponse.onSuccess(wateringTerrariumResponse));
    }
}
