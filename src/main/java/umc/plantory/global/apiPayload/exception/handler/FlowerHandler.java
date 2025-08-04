package umc.plantory.global.apiPayload.exception.handler;

import umc.plantory.global.apiPayload.code.BaseErrorCode;
import umc.plantory.global.apiPayload.exception.GeneralException;

public class FlowerHandler extends GeneralException {
    public FlowerHandler(BaseErrorCode errorCode) {super(errorCode);}
}
