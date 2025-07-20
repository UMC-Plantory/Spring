package umc.plantory.domain.diary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.plantory.domain.diary.dto.request.DiaryRequestDTO;
import umc.plantory.domain.diary.dto.response.DiaryResponseDTO;
import umc.plantory.domain.diary.service.DiaryCommandService;
import umc.plantory.global.apiPayload.ApiResponse;

@Tag(name = "Diary", description = "일기 관련 API")
@RestController
@RequestMapping("/v1/plantory/diary")
@RequiredArgsConstructor
public class DiaryRestController {

    private final DiaryCommandService diaryCommandService;

    @Operation(
            summary = "일기 작성",
            description = "일기를 작성합니다. NORMAL로 저장할 경우 물뿌리개가 생성됩니다."
    )
    @PostMapping
    public ApiResponse<DiaryResponseDTO.DiaryInfoDTO> saveDiary(
            @Valid @RequestBody DiaryRequestDTO requestDTO
    ) {
        return ApiResponse.onSuccess(diaryCommandService.saveDiary(requestDTO));
    }
}