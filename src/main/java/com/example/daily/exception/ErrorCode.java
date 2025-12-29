package com.example.daily.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //TODO
    TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "T-001", "해당 할 일을 찾을 수 없습니다."),
    UNAUTHORIZED_UPDATE(HttpStatus.FORBIDDEN, "T-002", "본인의 할 일만 수정할 수 있습니다."),
    UNAUTHORIZED_DELETE(HttpStatus.FORBIDDEN, "T-003", "본인의 할 일만 삭제할 수 있습니다."),

    //유저
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U-001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "U-002", "이미 존재하는 아이디입니다."),

    //공통 값
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C-001", "잘못된 입력값입니다.");
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
