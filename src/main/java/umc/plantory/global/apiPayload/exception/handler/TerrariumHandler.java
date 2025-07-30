package umc.plantory.global.apiPayload.exception.handler;

import umc.plantory.global.apiPayload.code.status.ErrorStatus;

public class TerrariumHandler extends RuntimeException {
    private final ErrorStatus errorStatus;

    public TerrariumHandler(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }

    public ErrorStatus getErrorStatus() {
        return errorStatus;
    }
}
