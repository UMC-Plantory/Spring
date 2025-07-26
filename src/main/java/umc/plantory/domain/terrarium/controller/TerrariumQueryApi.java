package umc.plantory.domain.terrarium.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import umc.plantory.domain.terrarium.controller.dto.TerrariumResponseDto;
import umc.plantory.global.apiPayload.ApiResponse;

import java.util.List;

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

    @Operation(
            summary = "월별로 다 키운(개화 완료) 테라리움 조회",
            description = "회원 ID와 연도/월 정보를 기반으로 해당 달에 개화(완료)된 테라리움 리스트를 조회합니다."
    )
    ResponseEntity<ApiResponse<List<TerrariumResponseDto.CompletedTerrariumResponse>>> getCompletedTerrariumsByMonth(
            @Parameter(description = "회원 ID", example = "1")
            @RequestParam("memberId") Long memberId,
            @Parameter(description = "조회할 연도(YYYY)", example = "2025")
            @RequestParam(value = "year") int year,
            @Parameter(description = "조회할 월(MM)", example = "6")
            @RequestParam(value = "month") int month
    );

}
