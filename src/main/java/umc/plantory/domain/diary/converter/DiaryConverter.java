package umc.plantory.domain.diary.converter;

import umc.plantory.domain.diary.dto.DiaryRequestDTO;
import umc.plantory.domain.diary.dto.DiaryResponseDTO;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.diary.entity.DiaryImg;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.global.enums.DiaryStatus;
import umc.plantory.global.enums.Emotion;

import java.time.LocalDate;
import java.util.List;

public class DiaryConverter {

    public static Diary toDiary(DiaryRequestDTO.DiaryUploadDTO request, Member member, String title, String aiComment) {
        return Diary.builder()
                .title(title)
                .diaryDate(request.getDiaryDate())
                .emotion(request.getEmotion() != null ? Emotion.valueOf(request.getEmotion()) : null)
                .content(request.getContent())
                .sleepStartTime(request.getSleepStartTime())
                .sleepEndTime(request.getSleepEndTime())
                .status(DiaryStatus.valueOf(request.getStatus()))
                .member(member)
                .aiComment(aiComment)
                .build();
    }

    public static DiaryImg toDiaryImg(String imageUrl, Diary diary) {
        return DiaryImg.builder()
                .diary(diary)
                .diaryImgUrl(imageUrl)
                .build();
    }

    public static DiaryResponseDTO.DiaryInfoDTO toDiaryInfoDTO(Diary diary, String imageUrl) {
        return DiaryResponseDTO.DiaryInfoDTO.builder()
                .diaryId(diary.getId())
                .diaryDate(diary.getDiaryDate())
                .emotion(diary.getEmotion())
                .title(diary.getTitle())
                .content(diary.getContent())
                .diaryImgUrl(imageUrl)
                .status(diary.getStatus())
                .aiComment(diary.getAiComment())
                .build();
    }

    public static DiaryResponseDTO.DiarySimpleInfoDTO toDiarySimpleInfoDTO(Diary diary) {
        return DiaryResponseDTO.DiarySimpleInfoDTO.builder()
                .diaryId(diary.getId())
                .diaryDate(diary.getDiaryDate())
                .emotion(diary.getEmotion())
                .title(diary.getTitle())
                .build();
    }

    public static DiaryResponseDTO.DiaryExistsDTO toDiaryExistsDTO(boolean exists) {
        return DiaryResponseDTO.DiaryExistsDTO.builder()
                .isExist(exists)
                .build();
    }

    public static DiaryResponseDTO.DiaryListInfoDTO toDiaryListInfoDTO(Diary diary) {
        return DiaryResponseDTO.DiaryListInfoDTO.builder()
                .diaryId(diary.getId())
                .diaryDate(diary.getDiaryDate())
                .title(diary.getTitle())
                .status(diary.getStatus())
                .emotion(diary.getEmotion())
                .content(diary.getContent())
                .build();
    }

    public static <T> DiaryResponseDTO.CursorPaginationDTO<T> toCursorPaginationDTO(List<T> diaries, boolean hasNext, LocalDate nextCursor) {
        return DiaryResponseDTO.CursorPaginationDTO.<T>builder()
                .diaries(diaries)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .build();
    }

    public static <T> DiaryResponseDTO.CursorPaginationTotalDTO<T> toCursorPaginationWithTotalDTO(List<T> diaries, boolean hasNext, LocalDate nextCursor, long total) {
        return DiaryResponseDTO.CursorPaginationTotalDTO.<T>builder()
                .diaries(diaries)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .total(total)
                .build();
    }

    public static DiaryResponseDTO.DiaryListSimpleInfoDTO toDiayListSimpleInfoDTO(Diary diary) {
        return DiaryResponseDTO.DiaryListSimpleInfoDTO.builder()
                .diaryId(diary.getId())
                .diaryDate(diary.getDiaryDate())
                .title(diary.getTitle())
                .build();
    }

    public static DiaryResponseDTO.DiaryListDTO toDiaryListDTO(List<DiaryResponseDTO.DiaryListSimpleInfoDTO> diaries) {
        return DiaryResponseDTO.DiaryListDTO.builder()
                .diaries(diaries)
                .build();
    }
}
