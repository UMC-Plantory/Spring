package umc.plantory.domain.diary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.plantory.domain.diary.dto.DiaryResponseDTO;
import umc.plantory.domain.diary.service.DiaryQueryUseCase;
import umc.plantory.global.apiPayload.ApiResponse;

import java.time.LocalDate;

@Tag(name = "Diary", description = "일기 관련 API")
@RestController
@RequestMapping("/v1/plantory/diary")
@RequiredArgsConstructor
public class DiaryQueryController {

    private final DiaryQueryUseCase diaryQueryUseCase;

    @Operation(
            summary = "단일 일기 조회",
            description = "diaryId로 일기 데이터를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DIARY4001", description = "일기를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DIARY4003", description = "일기 작성자와 일치하지 않음", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/{diaryId}")
    public ResponseEntity<ApiResponse<DiaryResponseDTO.DiaryInfoDTO>> getDiary(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Parameter(description = "조회할 일기의 ID") @PathVariable("diaryId") Long diaryId
    ) {
        DiaryResponseDTO.DiaryInfoDTO response = diaryQueryUseCase.getDiaryInfo(authorization, diaryId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(
            summary = "특정 날짜 일기 조회 [홈 용]",
            description = "yyyy-MM-dd 형식의 날짜를 기반으로 해당 사용자의 작성된 일기(NORMAL, SCRAP) 요약 정보를 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DIARY4001", description = "일기를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/date")
    public ResponseEntity<ApiResponse<DiaryResponseDTO.DiarySimpleInfoDTO>> getDiarySimpleInfo(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Parameter(description = "조회할 날짜 (yyyy-MM-dd)") @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        DiaryResponseDTO.DiarySimpleInfoDTO response = diaryQueryUseCase.getDiarySimpleInfo(authorization, date);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(
            summary = "특정 날짜에 임시 보관중인 일기 존재 여부 확인",
            description = "yyyy-MM-dd 형식의 날짜를 받아 해당 날짜에 TEMP 상태의 일기가 존재하는지 확인합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공")
    })
    @GetMapping("/temp/check")
    public ResponseEntity<ApiResponse<DiaryResponseDTO.TempDiaryExistsDTO>> checkTempDiaryExistence(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Parameter(description = "조회할 날짜 (yyyy-MM-dd)") @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        DiaryResponseDTO.TempDiaryExistsDTO response = diaryQueryUseCase.checkTempDiaryExistence(authorization, date);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
