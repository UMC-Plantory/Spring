package umc.plantory.domain.terrarium.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.domain.terrarium.service.TerrariumQueryUseCase;
import umc.plantory.global.apiPayload.ApiResponse;

/**
 * 테라리움 관련 조회 API 컨트롤러
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/plantory")
public class TerrariumQueryController implements TerrariumQueryApi {

    private final TerrariumQueryUseCase terrariumQueryUseCase;

    /**
     * 현재 키우고 있는 테라리움 정보를 조회합니다.
     * @param memberId 회원 ID
     * @return 테라리움 정보 응답
     */
    @Override
    @GetMapping("/terrarium")
    public ResponseEntity<ApiResponse<TerrariumResponseDto.TerrariumResponse>> getTerrariumData(
            @RequestParam("memberId") Long memberId) {
        TerrariumResponseDto.TerrariumResponse currentTerrariumData = terrariumQueryUseCase.getCurrentTerrariumData(memberId);

        return ResponseEntity.ok(ApiResponse.onSuccess(currentTerrariumData));
    }
}
