package umc.plantory.apiPayload.exception.handler;

import umc.plantory.apiPayload.code.BaseErrorCode;
import umc.plantory.apiPayload.exception.GeneralException;

public class TempHandler extends GeneralException {
    public TempHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
