package umc.plantory.global.apiPayload.exception;

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

    public ErrorType getErrorType() {
        return errorType;
    }

}
