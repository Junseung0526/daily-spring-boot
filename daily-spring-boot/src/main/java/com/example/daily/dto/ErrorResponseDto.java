package com.example.daily.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ErrorResponseDto {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String code;
    private final String message;
    private final List<FieldError> errors;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class FieldError {
        private final String field;
        private final String value;
        private final String reason;
    }
}
