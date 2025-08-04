package umc.plantory.domain.terrarium.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.domain.terrarium.service.TerrariumQueryUseCase;
import umc.plantory.global.apiPayload.ApiResponse;

import java.time.YearMonth;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/plantory/terrarium")
public class TerrariumQueryController implements TerrariumQueryApi {

    private final TerrariumQueryUseCase terrariumQueryUseCase;

    /**
     * 현재 키우고 있는 테라리움을 조회합니다.
     *
     * @param authorization 인증용 JWT 토큰
     * @return ApiResponse로 감싼 현재 테라리움 정보 DTO
     */
    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<TerrariumResponseDto.TerrariumResponse>> getTerrariumData(
            @RequestHeader("Authorization") String authorization) {

        log.info("현재 테라리움 조회 요청");
        TerrariumResponseDto.TerrariumResponse response = terrariumQueryUseCase.findCurrentTerrariumData(authorization);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    /**
     * 회원의 월별 개화 완료된 테라리움 목록을 조회합니다.
     *
     * @param authorization 인증용 JWT 토큰
     * @param date bloom_at 조회할 연/월 (YYYY-MM)
     * @return ApiResponse로 감싼 개화 완료된 테라리움 목록 DTO 리스트
     */
    @Override
    @GetMapping("/month")
    public ResponseEntity<ApiResponse<List<TerrariumResponseDto.CompletedTerrariumResponse>>> getCompletedTerrariumsByMonth(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM") YearMonth date) {

        log.info("월별 개화 완료 테라리움 조회");
        List<TerrariumResponseDto.CompletedTerrariumResponse> responseList = terrariumQueryUseCase.findCompletedTerrariumsByMonth(authorization, date);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseList));
    }

    /**
     * 특정 완료된 테라리움의 상세 정보를 조회합니다.
     *
     * @param terrariumId 상세 조회할 테라리움 ID
     * @return ApiResponse로 감싼 해당 테라리움 상세 DTO
     */
    @Override
    @GetMapping("/{terrarium-id}")
    public ResponseEntity<ApiResponse<TerrariumResponseDto.CompletedTerrariumDetatilResponse>> getCompletedTerrariumDetail(
            @PathVariable("terrarium-id") Long terrariumId) {

        log.info("완료된 테라리움 상세 조회 - terrariumId: {}", terrariumId);
        TerrariumResponseDto.CompletedTerrariumDetatilResponse response = terrariumQueryUseCase.findCompletedTerrariumDetail(terrariumId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
