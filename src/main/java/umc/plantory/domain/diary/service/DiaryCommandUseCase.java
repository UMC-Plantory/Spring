package umc.plantory.domain.diary.service;

import umc.plantory.domain.diary.dto.DiaryRequestDTO;
import umc.plantory.domain.diary.dto.DiaryResponseDTO;

public interface DiaryCommandUseCase {
    DiaryResponseDTO.DiaryInfoDTO saveDiary(DiaryRequestDTO.DiaryUploadDTO request);
    DiaryResponseDTO.DiaryInfoDTO updateDiary(Long diaryId, DiaryRequestDTO.DiaryUpdateDTO request);
    void scrapDiary(Long diaryId);
    void cancelScrapDiary(Long diaryId);
    void tempSaveDiaries(DiaryRequestDTO.DiaryIdsDTO request);
    void softDeleteDiaries(DiaryRequestDTO.DiaryIdsDTO request);
    void hardDeleteDiaries(DiaryRequestDTO.DiaryIdsDTO request);
}
