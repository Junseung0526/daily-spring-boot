package com.example.daily.controller;

import com.example.daily.dto.LoginRequestDto;
import com.example.daily.dto.TokenResponseDto;
import com.example.daily.dto.UserRequestDto;
import com.example.daily.dto.UserResponseDto;
import com.example.daily.security.UserDetailsImpl;
import com.example.daily.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "유저 인증 API", description = "회원가입 및 로그인 등 인증 관련 기능을 제공합니다.")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService us;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public UserResponseDto signup(@Valid @RequestBody UserRequestDto dto) {
        return us.createUser(dto);
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        TokenResponseDto tokenResponseDto = us.login(loginRequestDto.getUsername(), loginRequestDto.getPassword());

        return ResponseEntity.ok(tokenResponseDto);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String accessToken,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        us.logout(accessToken, userDetails.getUsername());
        return ResponseEntity.ok("로그아웃되었습니다.");
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<String> reissue(@RequestHeader("RefreshToken") String refreshToken) {
        String newAccessToken = us.reissue(refreshToken);
        return ResponseEntity.ok(newAccessToken);
    }

    @Operation(summary = "유저 전체 조회")
    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return us.getAllUsers();
    }

    @Operation(summary = "유저 단건 조회")
    @GetMapping("/{userId}")
    public UserResponseDto getUser(@PathVariable Long userId) {
        return us.getUserDtoById(userId);
    }

    @Operation(summary = "유저 삭제", description = "유저 삭제시 관련된 모든 할 일도 함께 삭제됩니다.")
    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        us.deleteUser(userId);
        return "유저와 관련된 모든 할 일이 삭제되었습니다. ID: " + userId;
    }
}
