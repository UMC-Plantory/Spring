package umc.plantory.global.apiPayload.exception.handler;

import umc.plantory.global.apiPayload.code.BaseErrorCode;
import umc.plantory.global.apiPayload.exception.GeneralException;

public class PushHandler extends GeneralException {
    public PushHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
