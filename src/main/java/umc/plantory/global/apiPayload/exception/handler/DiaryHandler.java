package umc.plantory.global.apiPayload.exception.handler;

import umc.plantory.global.apiPayload.code.BaseErrorCode;
import umc.plantory.global.apiPayload.exception.GeneralException;

public class DiaryHandler extends GeneralException {
    public DiaryHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}