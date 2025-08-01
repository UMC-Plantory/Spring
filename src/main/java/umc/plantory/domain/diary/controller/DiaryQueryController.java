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
import umc.plantory.domain.diary.dto.DiaryRequestDTO;
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

    @Operation(summary = "일기 목록 필터 조회", description = "날짜, 감정, 정렬 기준 + 커서로 일기 리스트를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공")
    })
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<DiaryResponseDTO.CursorPaginationDTO<DiaryResponseDTO.DiaryListInfoDTO>>> getDiaryList(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @ModelAttribute DiaryRequestDTO.DiaryFilterDTO request
    ) {
        DiaryResponseDTO.CursorPaginationDTO<DiaryResponseDTO.DiaryListInfoDTO> response = diaryQueryUseCase.getDiaryList(authorization, request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "스크랩 일기 리스트 조회", description = "정렬 기준과 커서를 기준으로 스크랩한 일기 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공")
    })
    @GetMapping("/scrap")
    public ResponseEntity<ApiResponse<DiaryResponseDTO.CursorPaginationDTO<DiaryResponseDTO.DiaryListInfoDTO>>> getScrappedDiaries(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(value = "sort", defaultValue = "latest") String sort,
            @RequestParam(value = "cursor", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate cursor,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        DiaryResponseDTO.CursorPaginationDTO<DiaryResponseDTO.DiaryListInfoDTO> response = diaryQueryUseCase.getScrapDiaryList(authorization, sort, cursor, size);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "임시 보관 일기 리스트 조회", description = "임시 보관한 일기를 정렬 기준에 따라 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공")
    })
    @GetMapping("/temp")
    public ResponseEntity<ApiResponse<DiaryResponseDTO.DiaryListDTO>> getTempDiaryList(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(value = "sort", defaultValue = "latest") String sort
    ) {
        DiaryResponseDTO.DiaryListDTO response = diaryQueryUseCase.getTempDiaryList(authorization, sort);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "삭제된 일기 리스트 조회", description = "삭제한 일기를 정렬 기준에 따라 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공")
    })
    @GetMapping("/waste")
    public ResponseEntity<ApiResponse<DiaryResponseDTO.DiaryListDTO>> getDeletedDiaryList(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(value = "sort", defaultValue = "latest") String sort
    ) {
        DiaryResponseDTO.DiaryListDTO response = diaryQueryUseCase.getDeletedDiaryList(authorization, sort);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
