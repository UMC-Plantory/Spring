package umc.plantory.domain.terrarium.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.global.apiPayload.ApiResponse;

@Tag(name = "테라리움 API", description = "테라리움 관련 API")
public interface TerrariumCommandApi {

    @Operation(summary = "테라리움 물주기", description = "회원이 특정 테라리움에 물을 주는 요청 처리")
    ResponseEntity<ApiResponse<TerrariumResponseDto.WateringTerrariumResponse>> waterTerrarium(
            @Parameter(description = "테라리움 ID", example = "1") @PathVariable("terrarium-id") Long terrariumId,
            @Parameter(description = "JWT 토큰", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authorization
    );
}
