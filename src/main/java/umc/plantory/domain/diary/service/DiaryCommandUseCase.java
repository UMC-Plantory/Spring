package umc.plantory.domain.diary.service;

import umc.plantory.domain.diary.dto.DiaryRequestDTO;
import umc.plantory.domain.diary.dto.DiaryResponseDTO;

public interface DiaryCommandUseCase {
    DiaryResponseDTO.DiaryInfoDTO saveDiary(String authorization, DiaryRequestDTO.DiaryUploadDTO request);
    DiaryResponseDTO.DiaryInfoDTO updateDiary(String authorization, Long diaryId, DiaryRequestDTO.DiaryUpdateDTO request);
    void scrapDiary(String authorization, Long diaryId);
    void cancelScrapDiary(String authorization, Long diaryId);
    void tempSaveDiaries(String authorization, DiaryRequestDTO.DiaryIdsDTO request);
    void softDeleteDiaries(String authorization, DiaryRequestDTO.DiaryIdsDTO request);
    void hardDeleteDiaries(String authorization, DiaryRequestDTO.DiaryIdsDTO request);
}
