package umc.plantory.global.apiPayload.exception.handler;

import umc.plantory.global.apiPayload.code.BaseErrorCode;
import umc.plantory.global.apiPayload.exception.GeneralException;

public class KakaoHandler extends GeneralException {
    public KakaoHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
