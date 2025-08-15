package umc.plantory.global.apiPayload.exception.handler;

import umc.plantory.global.apiPayload.code.BaseErrorCode;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;
import umc.plantory.global.apiPayload.exception.GeneralException;

public class PaginationHandler extends GeneralException {
    public PaginationHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
