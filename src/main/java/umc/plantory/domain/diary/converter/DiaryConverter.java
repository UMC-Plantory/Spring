package umc.plantory.domain.diary.converter;

import umc.plantory.domain.diary.dto.DiaryRequestDTO;
import umc.plantory.domain.diary.dto.DiaryResponseDTO;
import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.diary.entity.DiaryImg;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.global.enums.DiaryStatus;
import umc.plantory.global.enums.Emotion;

public class DiaryConverter {

    public static Diary toDiary(DiaryRequestDTO.DiaryUploadDTO request, Member member) {
        return Diary.builder()
                .title("임시 제목")
                .diaryDate(request.getDiaryDate())
                .emotion(request.getEmotion() != null ? Emotion.valueOf(request.getEmotion()) : null)
                .content(request.getContent())
                .sleepStartTime(request.getSleepStartTime())
                .sleepEndTime(request.getSleepEndTime())
                .status(DiaryStatus.valueOf(request.getStatus()))
                .member(member)
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
                .build();
    }
}
