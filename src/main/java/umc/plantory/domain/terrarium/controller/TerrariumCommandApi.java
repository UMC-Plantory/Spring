package umc.plantory.domain.terrarium.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.global.apiPayload.ApiResponse;

/**
 * 테라리움 관련 커맨드(상태 변경) API 인터페이스입니다.
 * 테라리움에 물을 주는 등의 상태 변경 요청을 처리합니다.
 */
@Tag(name = "테라리움 API", description = "테라리움 관련 API")
public interface TerrariumCommandApi {
    @Operation(
        summary = "테라리움 물주기",
        description = "특정 테라리움(terrariumId)에 대해 회원(memberId)이 물을 주는 요청을 처리합니다."
    )
    ResponseEntity<ApiResponse<TerrariumResponseDto.WateringTerrariumResponse>> waterTerrarium(
            @Parameter(description = "테라리움 ID", example = "1")
            @PathVariable("terrarium-id") Long terrariumId,
            @Parameter(description = "회원 ID", example = "1")
            @RequestParam Long memberId
    );
}
