package umc.plantory.global.apiPayload.exception.handler;

import umc.plantory.global.apiPayload.code.BaseErrorCode;
import umc.plantory.global.apiPayload.exception.GeneralException;

public class StatisticHandler extends GeneralException {
    public StatisticHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
