package com.example.daily.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRequestDto {

    @Schema(description = "사용자 아이디", example = "admin")
    @NotBlank(message = "아이디는 필수입니다.")
    @Pattern(regexp = "^[a-z0-9]{4,10}$", message = "아이디는 알파벳 소문자(a~z), 숫자(0~9)로 구성된 4~10자여야 합니다.")
    private String username;

    @Schema(description = "비밀번호", example = "Abc1234!")
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]{8,15}$", message = "비밀번호는 알파벳 대소문자, 숫자, 특수문자로 구성된 8~15자여야 합니다.")
    private String password;

    @Schema(description = "이메일", example = "admin@example.com")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @Schema(description = "관리자 여부 (true일 경우 관리자로 가입 시도)", example = "false")
    private boolean admin = false;

    @Schema(description = "관리자 인증 토큰 (관리자 가입 시에만 입력)", example = "AAABnvxRVklrnYxKZ0aHgOTdqygdgHYiq")
    private String adminToken = "";
}
