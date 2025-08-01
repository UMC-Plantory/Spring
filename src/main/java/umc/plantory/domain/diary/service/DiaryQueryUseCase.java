package umc.plantory.domain.diary.service;

import umc.plantory.domain.diary.dto.DiaryRequestDTO;
import umc.plantory.domain.diary.dto.DiaryResponseDTO;

import java.time.LocalDate;

public interface  DiaryQueryUseCase {
    DiaryResponseDTO.DiaryInfoDTO getDiaryInfo(String authorization, Long diaryId);
    DiaryResponseDTO.DiarySimpleInfoDTO getDiarySimpleInfo(String authorization, LocalDate date);
    DiaryResponseDTO.TempDiaryExistsDTO checkTempDiaryExistence(String authorization, LocalDate date);
    DiaryResponseDTO.CursorPaginationDTO<DiaryResponseDTO.DiaryListInfoDTO> getDiaryList(String authorization, DiaryRequestDTO.DiaryFilterDTO request);
    DiaryResponseDTO.CursorPaginationDTO<DiaryResponseDTO.DiaryListInfoDTO> getScrappedDiaries(String authorization, String sort, LocalDate cursor, int size);
}
