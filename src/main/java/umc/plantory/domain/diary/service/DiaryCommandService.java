package umc.plantory.domain.diary.service;

import umc.plantory.domain.diary.dto.request.DiaryRequestDTO;
import umc.plantory.domain.diary.dto.response.DiaryResponseDTO;

public interface DiaryCommandService {
    DiaryResponseDTO.DiaryInfoDTO saveDiary(DiaryRequestDTO.DiaryUploadDTO request);
    DiaryResponseDTO.DiaryInfoDTO updateDiary(Long diaryId, DiaryRequestDTO.DiaryUpdateDTO request);
}
