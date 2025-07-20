package umc.plantory.global.apiPayload.exception.handler;

import lombok.Getter;
import umc.plantory.global.apiPayload.code.status.ErrorStatus;

@Getter
public class MemberHandler extends RuntimeException {
    private final ErrorStatus errorStatus;

    public MemberHandler(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }
} 