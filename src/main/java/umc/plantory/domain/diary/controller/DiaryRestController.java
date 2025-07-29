package umc.plantory.domain.diary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.plantory.domain.diary.dto.DiaryRequestDTO;
import umc.plantory.domain.diary.dto.DiaryResponseDTO;
import umc.plantory.domain.diary.service.DiaryCommandUseCase;
import umc.plantory.global.apiPayload.ApiResponse;

@Tag(name = "Diary", description = "일기 관련 API")
@RestController
@RequestMapping("/v1/plantory/diary")
@RequiredArgsConstructor
public class DiaryRestController {

    private final DiaryCommandUseCase diaryCommandUseCase;
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
            @Valid @RequestBody DiaryRequestDTO.DiaryUploadDTO requestDTO
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(diaryCommandUseCase.saveDiary(requestDTO)));
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
            @Parameter(description = "수정할 일기의 ID", required = true)
            @PathVariable("diaryId") Long diaryId,
            @Valid @RequestBody DiaryRequestDTO.DiaryUpdateDTO requestDTO
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(diaryCommandUseCase.updateDiary(diaryId, requestDTO)));
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
    @PatchMapping("/{diaryId}/scrap/on")
    public ResponseEntity<ApiResponse<Void>> scrapDiary(
            @Parameter(description = "스크랩할 일기의 ID") @PathVariable Long diaryId
    ) {
        diaryCommandUseCase.scrapDiary(diaryId);
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
    @PatchMapping("/{diaryId}/scrap/off")
    public ResponseEntity<ApiResponse<Void>> cancelScrapDiary(
            @Parameter(description = "스크랩 취소할 일기의 ID") @PathVariable Long diaryId
    ) {
        diaryCommandUseCase.cancelScrapDiary(diaryId);
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
    @PatchMapping("/temp")
    public ResponseEntity<ApiResponse<Void>> tempSaveDiaries(
            @Valid @RequestBody DiaryRequestDTO.DiaryIdsDTO requestDTO
    ) {
        diaryCommandUseCase.tempSaveDiaries(requestDTO);
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
    @PatchMapping("/waste")
    public ResponseEntity<ApiResponse<Void>> moveDiariesToTrash(
            @Valid @RequestBody DiaryRequestDTO.DiaryIdsDTO request
    ) {
        diaryCommandUseCase.softDeleteDiaries(request);
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
            @Valid @RequestBody DiaryRequestDTO.DiaryIdsDTO request
    ) {
        diaryCommandUseCase.hardDeleteDiaries(request);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }
}