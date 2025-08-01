package umc.plantory.domain.wateringCan.converter;

import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.member.entity.Member;
import umc.plantory.domain.wateringCan.entity.WateringCan;

public class WateringCanConverter {

    public static WateringCan toWateringCan(Diary diary, Member member) {
        return WateringCan.builder()
                .diary(diary)
                .member(member)
                .diaryDate(diary.getDiaryDate())
                .emotion(diary.getEmotion())
                .build();
    }
}