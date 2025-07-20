package umc.plantory.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import umc.plantory.domain.chat.controller.dto.ChatResponseDto;
import umc.plantory.global.apiPayload.ApiResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatQueryApi {
    @Operation(
        summary = "챗봇 채팅창 이전 대화 기록 최초 조회",
        description = "사용자가 챗봇과 나눈 이전 대화 중 가장 최근 6개를 조회합니다. " +
        "소셜로그인 구현 이전이므로 memberId는 쿼리 파라미터로 전달"
    )
    ResponseEntity<ApiResponse<List<ChatResponseDto.ChatResponse>>> getChatHistoryLatest(
        @Parameter(
            description = "회원 ID",
            example = "1"
        ) @RequestParam Long memberId
    );

    @Operation(
        summary = "최초 진입 이후, 챗봇 채팅창 이전 채팅 스크롤 조회",
        description = "스크롤 시, 기준 시각(before) 이전의 6개 채팅을 추가로 조회 " +
                      "소셜로그인 구현 이전이므로 memberId와 before(yyyy-MM-dd'T'HH:mm:ss)는 쿼리 파라미터로 전달"
    )
    ResponseEntity<ApiResponse<List<ChatResponseDto.ChatResponse>>> getChatHistoryBefore(
        @Parameter(
            description = "회원 ID",
            example = "1"
        ) @RequestParam Long memberId,
        @Parameter(
            description = "기준 시각 (최초  6개 조회 이후 스크롤 업 시에 데이터 조회용)",
            example = "2025-07-20T12:00:00",
            schema = @Schema(type = "string", format = "date-time")
        ) LocalDateTime before
    );
}
