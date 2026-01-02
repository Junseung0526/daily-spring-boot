package com.example.daily.exception;

import com.example.daily.dto.ErrorResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponseDto.builder()
                        .status(errorCode.getStatus().value())
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<ErrorResponseDto> handleRestApiException(RestApiException e) {
        return buildErrorResponse(e.getErrorCode());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .status(400)
                .body(ErrorResponseDto.builder()
                        .status(400)
                        .code("BAD_REQUEST")
                        .message(e.getMessage())
                        .errors(new ArrayList<>())
                        .build());
    }

    // 유효성 검사 상세화 리팩토링
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();

        // 모든 필드 에러를 상세 정보 리스트로 변환
        List<ErrorResponseDto.FieldError> fieldErrors = bindingResult.getFieldErrors().stream()
                .map(error -> ErrorResponseDto.FieldError.builder()
                        .field(error.getField())
                        .value(error.getRejectedValue() == null ? "" : error.getRejectedValue().toString())
                        .reason(error.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity
                .status(400)
                .body(ErrorResponseDto.builder()
                        .status(400)
                        .code(ErrorCode.INVALID_INPUT_VALUE.getCode())
                        .message("입력 데이터 검증에 실패했습니다.")
                        .errors(fieldErrors) // 상세 목록 포함
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAllException(Exception e) {
        return ResponseEntity
                .status(500)
                .body(ErrorResponseDto.builder()
                        .status(500)
                        .code("INTERNAL_SERVER_ERROR")
                        .message("서버 내부 오류가 발생했습니다.")
                        .build());
    }
}
