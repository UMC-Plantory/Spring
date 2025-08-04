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
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.global.apiPayload.ApiResponse;

@Tag(name = "테라리움 API", description = "테라리움 관련 API")
public interface TerrariumCommandApi {

    @Operation(summary = "테라리움 물주기", description = "회원이 특정 테라리움에 물을 주는 요청 처리")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "물주기 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 - 예: 아직 개화하지 않은 테라리움, 이미 진행 중인 테라리움 존재", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "멤버, 테라리움, 꽃 정보 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 에러 (WATERING_PROCESS_FAILED 등)", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    ResponseEntity<ApiResponse<TerrariumResponseDto.WateringTerrariumResponse>> waterTerrarium(
            @Parameter(description = "테라리움 ID", example = "1") @PathVariable("terrarium-id") Long terrariumId,
            @Parameter(description = "JWT 토큰")
            @RequestHeader(value = "Authorization", required = false) String authorization
    );
}
