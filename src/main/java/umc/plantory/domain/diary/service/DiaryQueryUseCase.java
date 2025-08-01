package umc.plantory.domain.diary.service;

import umc.plantory.domain.diary.dto.DiaryResponseDTO;

import java.time.LocalDate;

public interface  DiaryQueryUseCase {
    DiaryResponseDTO.DiaryInfoDTO getDiaryInfo(String authorization, Long diaryId);
    DiaryResponseDTO.DiarySimpleInfoDTO getDiarySimpleInfo(String authorization, LocalDate date);
    DiaryResponseDTO.TempDiaryExistsDTO checkTempDiaryExistence(String authorization, LocalDate date);
}
