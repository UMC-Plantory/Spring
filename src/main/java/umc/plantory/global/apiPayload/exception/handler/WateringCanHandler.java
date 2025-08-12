package umc.plantory.global.apiPayload.exception.handler;

import umc.plantory.global.apiPayload.code.BaseErrorCode;
import umc.plantory.global.apiPayload.exception.GeneralException;

public class WateringCanHandler extends GeneralException {
    public WateringCanHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
