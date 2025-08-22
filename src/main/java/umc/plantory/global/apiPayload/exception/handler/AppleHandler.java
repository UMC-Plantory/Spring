package umc.plantory.global.apiPayload.exception.handler;

import umc.plantory.global.apiPayload.code.BaseErrorCode;
import umc.plantory.global.apiPayload.exception.GeneralException;

public class AppleHandler extends GeneralException {
    public AppleHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
