package umc.plantory.global.apiPayload.exception.handler;

import umc.plantory.global.apiPayload.code.BaseErrorCode;
import umc.plantory.global.apiPayload.exception.GeneralException;

public class MemberHandler extends GeneralException {
    public MemberHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}