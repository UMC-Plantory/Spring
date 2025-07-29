package umc.plantory.global.apiPayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import umc.plantory.global.apiPayload.code.BaseErrorCode;
import umc.plantory.global.apiPayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 멤버 관련
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "존재하지 않는 회원입니다."),
    INVALID_MEMBER_INFO(HttpStatus.BAD_REQUEST, "MEMBER4002", "회원 필수 정보가 누락되었습니다."),

    // 약관 관련
    TERM_NOT_FOUND(HttpStatus.NOT_FOUND, "TERM4001", "존재하지 않는 약관입니다."),
    REQUIRED_TERM_NOT_AGREED(HttpStatus.BAD_REQUEST, "TERM4002", "필수 약관에 동의하지 않았습니다."),

    // KAKAO 관련
    INVALID_ID_TOKEN(HttpStatus.BAD_REQUEST, "KAKAO4001", "유효하지 않은 ID Token 입니다."),
    ERROR_ON_VERIFYING(HttpStatus.INTERNAL_SERVER_ERROR, "KAKAO4002", "카카오 토큰 검증 도중 에러 발생"),

    // JWT 관련
    INVALID_JWT_TOKEN(HttpStatus.BAD_REQUEST, "JWT4001", "유효하지 않은 JWT 토큰입니다."),
    EXPIRED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "JWT4002", "만료된 JWT 토큰입니다."),

    // S3 관련
    INVALID_EXTENSION(HttpStatus.BAD_REQUEST, "S34001", "허용되지 않은 파일 확장자입니다."),
    INVALID_FILENAME(HttpStatus.BAD_REQUEST, "S34002", "파일 이름이 유효하지 않습니다."),
    IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "S34003", "해당 이미지가 S3에 존재하지 않습니다."),

    // 약관 관련
    TERM_NOT_FOUND(HttpStatus.NOT_FOUND, "TERM4001", "존재하지 않는 약관입니다."),
    REQUIRED_TERM_NOT_AGREED(HttpStatus.BAD_REQUEST, "TERM4002", "필수 약관에 동의하지 않았습니다."),

    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
