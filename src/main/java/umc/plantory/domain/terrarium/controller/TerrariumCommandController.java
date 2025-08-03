package umc.plantory.domain.terrarium.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.domain.terrarium.service.TerrariumCommandUseCase;
import umc.plantory.global.apiPayload.ApiResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/plantory/terrarium")
@Slf4j
public class TerrariumCommandController implements TerrariumCommandApi{

    private final TerrariumCommandUseCase terrariumCommandUseCase;

    /**
     * 테라리움에 물을 주는 요청을 처리합니다.
     *
     * @param terrariumId 물을 줄 테라리움의 ID
     * @param authorization 인증용 JWT 토큰
     * @return 물주기 결과 및 테라리움 상태 정보
     */
    @Override
    @PostMapping("/{terrarium-id}/water")
    public ResponseEntity<ApiResponse<TerrariumResponseDto.WateringTerrariumResponse>> waterTerrarium(
            @PathVariable("terrarium-id") Long terrariumId,
            @RequestHeader("Authorization") String authorization) {

        log.info("테라리움 물주기 요청 - terrariumId: {}", terrariumId);
        TerrariumResponseDto.WateringTerrariumResponse response = terrariumCommandUseCase.performTerrariumWatering(authorization, terrariumId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
