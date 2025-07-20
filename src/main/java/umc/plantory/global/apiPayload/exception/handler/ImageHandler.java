package umc.plantory.global.apiPayload.exception.handler;

import umc.plantory.global.apiPayload.code.BaseErrorCode;
import umc.plantory.global.apiPayload.exception.GeneralException;

public class ImageHandler extends GeneralException {
    public ImageHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}