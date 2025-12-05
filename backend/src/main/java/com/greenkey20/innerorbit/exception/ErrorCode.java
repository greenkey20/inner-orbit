package com.greenkey20.innerorbit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 정의
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 Bad Request
    INVALID_INPUT_VALUE("잘못된 입력 값입니다.", HttpStatus.BAD_REQUEST),
    INVALID_STABILITY_VALUE("안정성 값은 0-100 사이여야 합니다.", HttpStatus.BAD_REQUEST),
    INVALID_GRAVITY_VALUE("그리움 강도 값은 0-100 사이여야 합니다.", HttpStatus.BAD_REQUEST),
    EMPTY_CONTENT("내용을 입력해주세요.", HttpStatus.BAD_REQUEST),

    // 404 Not Found
    LOG_ENTRY_NOT_FOUND("로그 엔트리를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR("데이터베이스 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus httpStatus;
}
