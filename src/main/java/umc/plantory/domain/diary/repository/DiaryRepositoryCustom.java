package umc.plantory.domain.diary.repository;

import umc.plantory.domain.diary.dto.DiaryRequestDTO;
import umc.plantory.domain.diary.entity.Diary;

import java.time.LocalDate;
import java.util.List;

public interface DiaryRepositoryCustom {
    List<Diary> findFilteredDiaries(Long memberId, DiaryRequestDTO.DiaryFilterDTO request);
    List<Diary> findScrappedDiaries(Long memberId, String sort, LocalDate cursor, int size);
    List<Diary> searchDiaries(Long memberId, String keyword, LocalDate cursor, int size);
    long countDiariesByKeyword(Long memberId, String keyword);
}
