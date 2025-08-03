package umc.plantory.global.apiPayload.exception.handler;

import umc.plantory.global.apiPayload.code.status.ErrorStatus;

public class FlowerHandler extends RuntimeException {
    private final ErrorStatus errorStatus;

    public FlowerHandler(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }
    public ErrorStatus getErrorStatus() {return errorStatus;}
}
