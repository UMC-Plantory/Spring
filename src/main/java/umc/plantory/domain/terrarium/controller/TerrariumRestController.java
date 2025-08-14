package umc.plantory.domain.terrarium.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.plantory.domain.terrarium.dto.TerrariumResponseDto;
import umc.plantory.domain.terrarium.service.TerrariumCommandUseCase;
import umc.plantory.domain.terrarium.service.TerrariumQueryUseCase;
import umc.plantory.global.apiPayload.ApiResponse;

import java.time.YearMonth;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/plantory/terrariums")
@Slf4j
@Tag(name = "테라리움 API", description = "테라리움 관련 API")
public class TerrariumRestController {
    private final TerrariumCommandUseCase terrariumCommandUseCase;
    private final TerrariumQueryUseCase terrariumQueryUseCase;

    // Command 관련

    @Operation(summary = "테라리움 물주기", description = "회원이 특정 테라리움에 물을 주는 요청 처리")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "물주기 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4001", description = "존재하지 않는 회원입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "TERRARIUM404", description = "존재하지 않는 테라리움입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "TERRARIUM4004", description = "이미 개화한 테라리움입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "W4002", description = "사용 가능한 물뿌리개가 없습니다."),
    })
    @PostMapping("/{terrarium-id}/waterings")
    public ResponseEntity<ApiResponse<TerrariumResponseDto.WateringTerrariumResponse>> waterTerrarium(
            @Parameter(description = "테라리움 ID", example = "1") @PathVariable("terrarium-id") Long terrariumId,
            @Parameter(description = "JWT 토큰") @RequestHeader(value = "Authorization", required = false) String authorization) {

        log.info("테라리움 물주기 요청 - terrariumId: {}", terrariumId);
        TerrariumResponseDto.WateringTerrariumResponse response = terrariumCommandUseCase.performTerrariumWatering(authorization, terrariumId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }


    // Query 관련

    @Operation(summary = "현재 키우고 있는 테라리움 조회", description = "현재 키우고 있는 테라리움을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "정상 조회"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "JWT 인증 실패", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "테라리움/꽃 정보 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping
    public ResponseEntity<ApiResponse<TerrariumResponseDto.TerrariumResponse>> getTerrariumData(
            @Parameter(description = "JWT 토큰")
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        log.info("현재 테라리움 조회 요청");
        TerrariumResponseDto.TerrariumResponse response = terrariumQueryUseCase.findCurrentTerrariumData(authorization);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }


    @Operation(summary = "월별 개화 완료 테라리움 조회", description = "연도/월을 기준으로 개화 완료된 테라리움을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "정상 조회"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "JWT 인증 실패", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<List<TerrariumResponseDto.CompletedTerrariumResponse>>> getCompletedTerrariumsByMonth(
            @Parameter(description = "JWT 토큰")
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Parameter(description = "조회할 날짜 (yyyy-MM)") @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM") YearMonth date) {

        log.info("월별 개화 완료 테라리움 조회");
        List<TerrariumResponseDto.CompletedTerrariumResponse> responseList = terrariumQueryUseCase.findCompletedTerrariumsByMonth(authorization, date);
        return ResponseEntity.ok(ApiResponse.onSuccess(responseList));
    }


    @Operation(
            summary = "개화 완료 테라리움 상세 정보 페이지 조회",
            description = "테라리움 ID로 테라리움 상세 정보 페이지를 조회합니다. " +
                    "꽃 사진과 꽃 이름은 월별 개화 완료 조회 시 이미 제공되므로, " +
                    "상세 정보 페이지 조회 시 해당 정보를 활용"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "정상 조회"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "아직 개화하지 않은 테라리움", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "테라리움 또는 물뿌리개 정보 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/{terrarium-id}")
    public ResponseEntity<ApiResponse<TerrariumResponseDto.CompletedTerrariumDetailResponse>> getCompletedTerrariumDetail(
            @Parameter(description = "테라리움 ID", example = "1") @PathVariable("terrarium-id") Long terrariumId) {

        log.info("완료된 테라리움 상세 조회 - terrariumId: {}", terrariumId);
        TerrariumResponseDto.CompletedTerrariumDetailResponse response = terrariumQueryUseCase.findCompletedTerrariumDetail(terrariumId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
