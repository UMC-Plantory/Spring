package umc.plantory.domain.terrarium.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "현재 키우고 있는 테라리움 조회", description = "현재 키우고 있는 테라리움을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "정상 조회"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "테라리움/멤버/꽃 정보 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    ResponseEntity<ApiResponse<TerrariumResponseDto.TerrariumResponse>> getTerrariumData(
            @Parameter(description = "JWT 토큰", required = true)
            @RequestHeader("Authorization") String authorization);

    @Operation(summary = "월별 개화 완료 테라리움 조회", description = "연도/월을 기준으로 개화 완료된 테라리움을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "정상 조회"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "멤버 정보 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    ResponseEntity<ApiResponse<List<TerrariumResponseDto.CompletedTerrariumResponse>>> getCompletedTerrariumsByMonth(
            @Parameter(description = "JWT 토큰", required = true)
            @RequestHeader("Authorization") String authorization,
            @Parameter(description = "bloom_at, 연도 (YYYY)", example = "2025") @RequestParam("year") int year,
            @Parameter(description = "bloom_at, 월 (MM)", example = "6") @RequestParam("month") int month);

    @Operation(summary = "개화 완료 테라리움 상세 정보 페이지 조회", description = "테라리움 ID로 테라리움 상세 정보 페이지를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "정상 조회"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "아직 개화하지 않은 테라리움", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "테라리움 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    ResponseEntity<ApiResponse<TerrariumResponseDto.CompletedTerrariumDetatilResponse>> getCompletedTerrariumDetail(
            @Parameter(description = "테라리움 ID", example = "1") @PathVariable("terrarium-id") Long terrariumId);
}
