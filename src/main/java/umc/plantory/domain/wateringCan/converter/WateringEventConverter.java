package umc.plantory.domain.wateringCan.converter;


import umc.plantory.domain.terrarium.entity.Terrarium;
import umc.plantory.domain.wateringCan.entity.WateringCan;
import umc.plantory.domain.wateringCan.entity.WateringEvent;

public class WateringEventConverter {
    public static WateringEvent toWateringEvent(WateringCan wateringCan, Terrarium terrarium) {
        return WateringEvent.builder()
                .wateringCan(wateringCan)
                .terrarium(terrarium)
                .build();
    }
}
