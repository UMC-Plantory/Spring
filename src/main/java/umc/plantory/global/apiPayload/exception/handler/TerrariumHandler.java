package umc.plantory.global.apiPayload.exception.handler;

import umc.plantory.global.apiPayload.code.BaseErrorCode;
import umc.plantory.global.apiPayload.exception.GeneralException;

public class TerrariumHandler extends GeneralException {
    public TerrariumHandler(BaseErrorCode errorCode) {super(errorCode);}
}
