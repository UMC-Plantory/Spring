package umc.plantory.domain.terrarium.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.domain.terrarium.service.TerrariumQueryUseCase;
import umc.plantory.global.apiPayload.ApiResponse;

import java.util.List;

/**
 * 테라리움 관련 조회 API 컨트롤러
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/plantory/terrarium")
public class TerrariumQueryController implements TerrariumQueryApi {

    private final TerrariumQueryUseCase terrariumQueryUseCase;

    /**
     * 현재 키우고 있는 테라리움 정보를 조회합니다.
     * @param memberId 회원 ID
     * @return 테라리움 정보 응답
     */
    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<TerrariumResponseDto.TerrariumResponse>> getTerrariumData(
            @RequestParam("memberId") Long memberId) {
        TerrariumResponseDto.TerrariumResponse currentTerrariumData = terrariumQueryUseCase.findCurrentTerrariumData(memberId);

        return ResponseEntity.ok(ApiResponse.onSuccess(currentTerrariumData));
    }

    /**
     * 월별로 다 키운(개화 완료) 테라리움 목록을 조회합니다.
     *
     * @param memberId 조회할 회원의 ID
     * @param year 조회할 연도 (예: 2025)
     * @param month 조회할 월 (1~12)
     * @return 지정한 회원이 해당 연도와 월에 개화 완료한 테라리움들의 리스트를 포함한 ApiResponse 반환
     */
    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<List<TerrariumResponseDto.CompletedTerrariumResponse>>> getCompletedTerrariumsByMonth(
            @RequestParam("memberId") Long memberId,
            @RequestParam(value = "year") int year,
            @RequestParam(value = "month") int month) {
        List<TerrariumResponseDto.CompletedTerrariumResponse> completedTerrariumsByMonth = terrariumQueryUseCase.findCompletedTerrariumsByMonth(memberId, year, month);
        return ResponseEntity.ok(ApiResponse.onSuccess(completedTerrariumsByMonth));
    }
}
