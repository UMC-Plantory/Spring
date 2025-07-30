package umc.plantory.domain.terrarium.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.domain.terrarium.service.TerrariumQueryUseCase;
import umc.plantory.global.apiPayload.ApiResponse;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/plantory/terrarium")
public class TerrariumQueryController implements TerrariumQueryApi {

    private final TerrariumQueryUseCase terrariumQueryUseCase;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<TerrariumResponseDto.TerrariumResponse>> getTerrariumData(@RequestParam("memberId") Long memberId) {
        log.info("현재 테라리움 조회 요청 - memberId: {}", memberId);
        TerrariumResponseDto.TerrariumResponse response = terrariumQueryUseCase.findCurrentTerrariumData(memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/completed")
    public ResponseEntity<ApiResponse<List<TerrariumResponseDto.CompletedTerrariumResponse>>> getCompletedTerrariumsByMonth(
            @RequestParam("memberId") Long memberId,
            @RequestParam int year,
            @RequestParam int month) {

        log.info("월별 개화 완료 테라리움 조회 - memberId: {}, year: {}, month: {}", memberId, year, month);
        List<TerrariumResponseDto.CompletedTerrariumResponse> responseList = terrariumQueryUseCase.findCompletedTerrariumsByMonth(memberId, year, month);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseList));
    }

    @Override
    @GetMapping("/completed/{terrarium-id}")
    public ResponseEntity<ApiResponse<TerrariumResponseDto.CompletedTerrariumDetatilResponse>> getCompletedTerrariumDetail(
            @RequestParam("memberId") Long memberId,
            @PathVariable("terrarium-id") Long terrariumId) {

        log.info("완료된 테라리움 상세 조회 - memberId: {}, terrariumId: {}", memberId, terrariumId);
        TerrariumResponseDto.CompletedTerrariumDetatilResponse response = terrariumQueryUseCase.findCompletedTerrariumDetail(memberId, terrariumId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
