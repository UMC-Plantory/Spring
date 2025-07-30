package umc.plantory.domain.terrarium.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.global.apiPayload.ApiResponse;

import java.util.List;

@Tag(name = "테라리움 API", description = "테라리움 관련 API")
public interface TerrariumQueryApi {

    @Operation(summary = "현재 키우고 있는 테라리움 조회", description = "회원 ID를 기반으로 현재 키우고 있는 테라리움을 조회합니다.")
    ResponseEntity<ApiResponse<TerrariumResponseDto.TerrariumResponse>> getTerrariumData(
            @Parameter(description = "JWT 토큰", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authorization);

    @Operation(summary = "월별 개화 완료 테라리움 조회", description = "회원 ID와 연도/월을 기준으로 개화 완료된 테라리움을 조회합니다.")
    ResponseEntity<ApiResponse<List<TerrariumResponseDto.CompletedTerrariumResponse>>> getCompletedTerrariumsByMonth(
            @Parameter(description = "JWT 토큰", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authorization,
            @Parameter(description = "연도 (YYYY)", example = "2025") @RequestParam("year") int year,
            @Parameter(description = "월 (MM)", example = "6") @RequestParam("month") int month);

    @Operation(summary = "완료 테라리움 상세 조회", description = "회원 ID와 테라리움 ID로 완료된 테라리움 상세를 조회합니다.")
    ResponseEntity<ApiResponse<TerrariumResponseDto.CompletedTerrariumDetatilResponse>> getCompletedTerrariumDetail(
            @Parameter(description = "JWT 토큰", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authorization,
            @Parameter(description = "테라리움 ID", example = "1") @PathVariable("terrarium-id") Long terrariumId);
}
