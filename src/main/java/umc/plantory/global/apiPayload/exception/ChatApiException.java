package umc.plantory.global.apiPayload.exception;

import umc.plantory.global.apiPayload.code.status.ErrorStatus;

public class ChatApiException extends RuntimeException{
    private final ErrorType errorType;

    public enum ErrorType{
        INVALID_API_KEY,
        QUOTA_EXCEEDED,
        SERVER_ERROR
    }

    public ChatApiException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public ChatApiException(ErrorType errorType) {
        super();
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public ErrorStatus getErrorStatus() {
        return switch (errorType) {
            case INVALID_API_KEY -> umc.plantory.global.apiPayload.code.status.ErrorStatus._UNAUTHORIZED;
            case QUOTA_EXCEEDED -> umc.plantory.global.apiPayload.code.status.ErrorStatus._BAD_REQUEST;
            case SERVER_ERROR -> umc.plantory.global.apiPayload.code.status.ErrorStatus._INTERNAL_SERVER_ERROR;
            default -> umc.plantory.global.apiPayload.code.status.ErrorStatus._BAD_REQUEST;
        };
    }
    public String getErrorMessage() {
        return switch (errorType) {
            case INVALID_API_KEY -> "API 키가 잘못되었습니다.";
            case QUOTA_EXCEEDED -> "API 쿼터가 모두 소진되었습니다.";
            case SERVER_ERROR -> "OpenAI 서버에 일시적 장애가 발생했습니다. 잠시 후 다시 시도해 주세요.";
            default -> "알 수 없는 오류가 발생했습니다.";
        };
    }
}
