package umc.plantory.domain.wateringCan.converter;

import umc.plantory.domain.diary.entity.Diary;
import umc.plantory.domain.wateringCan.entity.WateringCan;

public class WateringCanConverter {

    public static WateringCan toWateringCan(Diary diary) {
        return WateringCan.builder()
                .diary(diary)
                .build();
    }
}