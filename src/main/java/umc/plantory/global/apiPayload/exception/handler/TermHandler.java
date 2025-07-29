package umc.plantory.global.apiPayload.exception.handler;

import umc.plantory.global.apiPayload.code.BaseErrorCode;
import umc.plantory.global.apiPayload.exception.GeneralException;

public class TermHandler extends GeneralException {
    public TermHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}