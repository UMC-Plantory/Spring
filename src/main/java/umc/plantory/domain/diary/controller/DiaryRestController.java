package umc.plantory.domain.diary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.plantory.domain.diary.dto.DiaryRequestDTO;
import umc.plantory.domain.diary.dto.DiaryResponseDTO;
import umc.plantory.domain.diary.service.DiaryCommandUseCase;
import umc.plantory.domain.diary.service.DiaryQueryUseCase;
import umc.plantory.global.apiPayload.ApiResponse;

import java.time.LocalDate;

@Tag(name = "Diary", description = "일기 관련 API")
@RestController
@RequestMapping("/v1/plantory/diaries")
@RequiredArgsConstructor
public class DiaryRestController {

    private final DiaryCommandUseCase diaryCommandUseCase;
    private final DiaryQueryUseCase diaryQueryUseCase;

    @Operation(
            summary = "일기 작성",
            description = "일기를 작성합니다. status가 NORMAL일 경우 물뿌리개가 생성됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청 형식",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "S34003", description = "이미지가 S3에 등록되지 않음",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @PostMapping
    public ResponseEntity<ApiResponse<DiaryResponseDTO.DiaryInfoDTO>> saveDiary(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody DiaryRequestDTO.DiaryUploadDTO requestDTO
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(diaryCommandUseCase.saveDiary(authorization, requestDTO)));
    }

    @Operation(
            summary = "일기 수정",
            description = "일기를 수정합니다. 기존 이미지를 삭제할 경우 isImgDeleted를 true로 해주세요."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DIARY4001", description = "일기를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DIARY4002", description = "필수 필드 미입력", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DIARY4003", description = "일기 작성자와 일치하지 않음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "S34003", description = "이미지가 S3에 등록되지 않음", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PatchMapping("/{diaryId}")
    public ResponseEntity<ApiResponse<DiaryResponseDTO.DiaryInfoDTO>> updateDiary(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Parameter(description = "수정할 일기의 ID", required = true)
            @PathVariable("diaryId") Long diaryId,
            @Valid @RequestBody DiaryRequestDTO.DiaryUpdateDTO requestDTO
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(diaryCommandUseCase.updateDiary(authorization, diaryId, requestDTO)));
    }

    @Operation(
            summary = "일기 스크랩",
            description = "NORMAL 상태인 일기를 SCRAP 상태로 변경합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DIARY4001", description = "일기를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DIARY4003", description = "일기 작성자와 일치하지 않음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DIARY4004", description = "현재 상태에서 스크랩할 수 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PatchMapping("/{diaryId}/scrap-status/on")
    public ResponseEntity<ApiResponse<Void>> scrapDiary(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Parameter(description = "스크랩할 일기의 ID") @PathVariable Long diaryId
    ) {
        diaryCommandUseCase.scrapDiary(authorization, diaryId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(
            summary = "스크랩 취소",
            description = "SCRAP 상태인 일기를 NORMAL 상태로 변경합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DIARY4001", description = "일기를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DIARY4003", description = "일기 작성자와 일치하지 않음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DIARY4004", description = "SCRAP 상태가 아니라 취소할 수 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PatchMapping("/{diaryId}/scrap-status/off")
    public ResponseEntity<ApiResponse<Void>> cancelScrapDiary(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Parameter(description = "스크랩 취소할 일기의 ID") @PathVariable Long diaryId
    ) {
        diaryCommandUseCase.cancelScrapDiary(authorization, diaryId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(
            summary = "일기 임시 보관",
            description = "전달받은 일기 ID 목록을 TEMP 상태로 일괄 변경합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DIARY4001", description = "일기를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DIARY4003", description = "일기 작성자와 일치하지 않음", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PatchMapping("/temp-status")
    public ResponseEntity<ApiResponse<Void>> tempSaveDiaries(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody DiaryRequestDTO.DiaryIdsDTO requestDTO
    ) {
        diaryCommandUseCase.tempSaveDiaries(authorization, requestDTO);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(
            summary = "일기 휴지통 이동",
            description = "전달받은 일기 ID 목록을 DELETE 상태로 일괄 변경합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DIARY4001", description = "일기를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DIARY4003", description = "일기 작성자와 일치하지 않음", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PatchMapping("/waste-status")
    public ResponseEntity<ApiResponse<Void>> moveDiariesToTrash(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody DiaryRequestDTO.DiaryIdsDTO request
    ) {
        diaryCommandUseCase.softDeleteDiaries(authorization, request);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(
            summary = "일기 영구 삭제",
            description = "전달받은 일기 ID 목록을 DB에서 완전히 삭제합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DIARY4001", description = "일기를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "DIARY4003", description = "일기 작성자와 일치하지 않음", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> hardDeleteDiaries(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody DiaryRequestDTO.DiaryIdsDTO request
    ) {
        diaryCommandUseCase.hardDeleteDiaries(authorization, request);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

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
    @GetMapping("/temp-status/exists")
    public ResponseEntity<ApiResponse<DiaryResponseDTO.DiaryExistsDTO>> checkTempDiaryExistence(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Parameter(description = "조회할 날짜 (yyyy-MM-dd)") @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        DiaryResponseDTO.DiaryExistsDTO response = diaryQueryUseCase.checkTempDiaryExistence(authorization, date);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(
            summary = "특정 날짜에 정식 저장 일기 존재 여부 확인",
            description = "yyyy-MM-dd 형식의 날짜를 받아 해당 날짜에 NORMAL 상태의 일기가 존재하는지 확인합니다."
    )
    @GetMapping("/normal-status/exists")
    public ResponseEntity<ApiResponse<DiaryResponseDTO.DiaryExistsDTO>> checkNormalDiaryExistence(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Parameter(description = "조회할 날짜 (yyyy-MM-dd)") @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        DiaryResponseDTO.DiaryExistsDTO response = diaryQueryUseCase.checkNormalDiaryExistence(authorization, date);
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

    @Operation(summary = "일기 검색", description = "제목 또는 내용에 특정 키워드가 포함된 일기 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공")
    })
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<DiaryResponseDTO.CursorPaginationTotalDTO<DiaryResponseDTO.DiaryListInfoDTO>>> searchDiaries(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "cursor", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate cursor,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        DiaryResponseDTO.CursorPaginationTotalDTO<DiaryResponseDTO.DiaryListInfoDTO> response = diaryQueryUseCase.searchDiaries(authorization, keyword, cursor, size);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "스크랩 일기 리스트 조회", description = "정렬 기준과 커서를 기준으로 스크랩한 일기 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공")
    })
    @GetMapping("/scrap-status")
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
    @GetMapping("/temp-status")
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
    @GetMapping("/waste-status")
    public ResponseEntity<ApiResponse<DiaryResponseDTO.DiaryListDTO>> getDeletedDiaryList(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(value = "sort", defaultValue = "latest") String sort
    ) {
        DiaryResponseDTO.DiaryListDTO response = diaryQueryUseCase.getDeletedDiaryList(authorization, sort);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}