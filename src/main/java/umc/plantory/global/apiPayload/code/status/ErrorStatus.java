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
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4001", "존재하지 않는 회원입니다."),
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
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "JWT4003", "유효하지 않은 리프레시 토큰입니다. 재로그인이 필요합니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "JWT4004", "만료된 리프레시 토큰입니다. 재로그인이 필요합니다."),

    // S3 관련
    INVALID_EXTENSION(HttpStatus.BAD_REQUEST, "S34001", "허용되지 않은 파일 확장자입니다."),
    INVALID_FILENAME(HttpStatus.BAD_REQUEST, "S34002", "파일 이름이 유효하지 않습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "S34003", "해당 이미지가 S3에 존재하지 않습니다."),
  
    // 통계 관련
    SLEEP_STATISTIC_NOT_FOUND(HttpStatus.BAD_REQUEST, "STATISTIC4001", "수면 통계가 존재하지 않습니다."),
    EMOTION_STATISTIC_NOT_FOUND(HttpStatus.BAD_REQUEST, "STATISTIC4002", "감정 통계가 존재하지 않습니다."),

    // 일기 관련
    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "DIARY4001", "존재하지 않는 일기입니다."),
    DIARY_MISSING_FIELDS(HttpStatus.BAD_REQUEST, "DIARY4002", "일기의 필수 항목이 누락되었습니다."),
    DIARY_UNAUTHORIZED(HttpStatus.FORBIDDEN,"DIARY4003","해당 일기에 대한 권한이 없습니다."),
    DIARY_INVALID_STATUS(HttpStatus.BAD_REQUEST, "DIARY4004", "현재 일기의 상태에서는 해당 작업을 수행할 수 없습니다."),

    // 채팅 관련
    INVALID_API_KEY(HttpStatus.UNAUTHORIZED, "CHAT4001", "API 키가 잘못됐습니다."),
    QUOTA_EXCEEDED(HttpStatus.FORBIDDEN, "CHAT4002", "API 쿼터가 모두 소진되었습니다."),
    OPEN_AI_SERVER_ERROR(HttpStatus.TOO_MANY_REQUESTS, "CHAT4003", "OPENAI 서버 오류."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "CHAT4004", "서버 오류"),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "CHAT4005", "서버 과부하"),
      
    // 테라리움 관련
    TERRARIUM_NOT_FOUND(HttpStatus.NOT_FOUND, "TERRARIUM404", "존재하지 않는 테라리움입니다."),
    INVALID_MEMBER_ID(HttpStatus.BAD_REQUEST, "TERRARIUM4001", "유효하지 않은 회원 ID입니다."),
    MEMBER_HAS_NO_TERRARIUM(HttpStatus.NOT_FOUND, "TERRARIUM4041", "해당 회원의 테라리움이 존재하지 않습니다."),
    FLOWER_NOT_FOUND_IN_TERRARIUM(HttpStatus.NOT_FOUND, "TERRARIUM4042", "테라리움에 꽃 정보가 존재하지 않습니다."),
    FLOWER_IMG_NOT_FOUND_IN_TERRARIUM(HttpStatus.NOT_FOUND, "TERRARIUM4043", "테라리움에 꽃 이미지 정보가 존재하지 않습니다."),
    TERRARIUM_NOT_BLOOMED(HttpStatus.BAD_REQUEST, "TERRARIUM4002", "아직 개화하지 않은 테라리움입니다."),
    NO_COMPLETED_TERRARIUM_IN_MONTH(HttpStatus.NOT_FOUND, "TERRARIUM4044", "해당 월에 개화 완료된 테라리움이 없습니다."),
    TERRARIUM_ALREADY_IN_PROGRESS(HttpStatus.BAD_REQUEST, "TERRARIUM4003", "이미 진행 중인 테라리움이 존재합니다."),

    // 물주기 관련
    WATERING_PROCESS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "W4001", "물 주기에 실패했습니다."),
    NO_AVAILABLE_WATERING_CAN(HttpStatus.BAD_REQUEST, "W4002", "사용 가능한 물뿌리개가 없습니다."),
    WATERING_CAN_NOT_FOUND(HttpStatus.NOT_FOUND, "W404", "물뿌리개가 존재하지 않습니다."),

    // 꽃 관련
    FLOWER_NOT_FOUND(HttpStatus.NOT_FOUND, "FLOWER404", "존재하지 않는 꽃입니다."),

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
