package umc.plantory.domain.terrarium.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.global.apiPayload.ApiResponse;

@Tag(name = "테라리움 API", description = "테라리움 관련 API")
public interface TerrariumQueryApi {
    @Operation(
        summary = "현재 키우고 있는 테라리움 조회",
        description = "회원 ID를 기반으로 현재 키우고 있는 테라리움의 정보를 조회합니다."
    )
    ResponseEntity<ApiResponse<TerrariumResponseDto.TerrariumResponse>> getTerrariumData(
        @Parameter(
            description = "회원 ID",
            example = "1"
        )
        @RequestParam Long memberId
    );
}
