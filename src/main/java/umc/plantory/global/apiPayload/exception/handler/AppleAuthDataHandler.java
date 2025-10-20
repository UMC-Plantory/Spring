package umc.plantory.global.apiPayload.exception.handler;

import umc.plantory.global.apiPayload.code.BaseErrorCode;
import umc.plantory.global.apiPayload.exception.GeneralException;

public class AppleAuthDataHandler extends GeneralException {
    public AppleAuthDataHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
