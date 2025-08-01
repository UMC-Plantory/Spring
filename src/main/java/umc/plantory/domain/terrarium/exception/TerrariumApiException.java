package umc.plantory.domain.terrarium.exception;

import umc.plantory.global.apiPayload.code.status.ErrorStatus;

import static umc.plantory.global.apiPayload.code.status.ErrorStatus.*;

public class TerrariumApiException extends RuntimeException {
    private final ErrorType errorType;

    public enum ErrorType {
        TERRARIUM_NOT_FOUND,
        INVALID_MEMBER_ID,
        MEMBER_HAS_NO_TERRARIUM,
        FLOWER_NOT_FOUND_IN_TERRARIUM
    }

    public TerrariumApiException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public TerrariumApiException(ErrorType errorType) {
        super();
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public ErrorStatus getErrorStatus() {
        return switch (errorType) {
            case TERRARIUM_NOT_FOUND -> _BAD_REQUEST;
            case INVALID_MEMBER_ID -> _BAD_REQUEST;
            case MEMBER_HAS_NO_TERRARIUM -> _BAD_REQUEST;
            case FLOWER_NOT_FOUND_IN_TERRARIUM -> _BAD_REQUEST;
            default -> _BAD_REQUEST;
        };
    }

    public String getErrorMessage() {
        return switch (errorType) {
            case TERRARIUM_NOT_FOUND -> "존재하지 않는 테라리움입니다.";
            case INVALID_MEMBER_ID -> "유효하지 않은 회원 ID입니다.";
            case MEMBER_HAS_NO_TERRARIUM -> "해당 회원의 테라리움이 존재하지 않습니다.";
            case FLOWER_NOT_FOUND_IN_TERRARIUM -> "테라리움에서 꽃을 찾을 수 없습니다.";
            default -> "알 수 없는 오류가 발생했습니다.";
        };
    }
}
