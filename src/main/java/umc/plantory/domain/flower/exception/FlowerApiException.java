package umc.plantory.domain.flower.exception;

import umc.plantory.global.apiPayload.code.status.ErrorStatus;

import static umc.plantory.global.apiPayload.code.status.ErrorStatus.*;

public class FlowerApiException extends RuntimeException {
    private final ErrorType errorType;

    public enum ErrorType {
        FLOWER_NOT_FOUND,
        INVALID_FLOWER_ID,
        FLOWER_IMG_NOT_FOUND
    }

    public FlowerApiException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public FlowerApiException(ErrorType errorType) {
        super();
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public ErrorStatus getErrorStatus() {
        return switch (errorType) {
            case FLOWER_NOT_FOUND -> _BAD_REQUEST;
            case INVALID_FLOWER_ID -> _BAD_REQUEST;
            case FLOWER_IMG_NOT_FOUND -> _BAD_REQUEST;
            default -> _BAD_REQUEST;
        };
    }

    public String getErrorMessage() {
        return switch (errorType) {
            case FLOWER_NOT_FOUND -> "존재하지 않는 꽃입니다.";
            case INVALID_FLOWER_ID -> "유효하지 않은 꽃 ID입니다.";
            case FLOWER_IMG_NOT_FOUND -> "꽃 이미지를 찾을 수 없습니다.";
            default -> "알 수 없는 오류가 발생했습니다.";
        };
    }
}
