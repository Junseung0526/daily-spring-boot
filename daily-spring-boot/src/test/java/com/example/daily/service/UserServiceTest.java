package com.example.daily.service;

import com.example.daily.dto.TokenResponseDto;
import com.example.daily.entity.User;
import com.example.daily.entity.UserRoleEnum;
import com.example.daily.repository.UserRepository;
import com.example.daily.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtUtil jwtUtil;
    @Mock RedisTemplate<String, String> redisTemplate;
    @Mock ValueOperations<String, String> valueOperations;

    @InjectMocks UserService userService;

    @Test
    @DisplayName("로그인 성공 - 토큰 발급 및 Redis 저장 확인")
    void login_Success() {
        String username = "admin";
        String password = "password";
        User user = new User(username, "encoded_pass", "test@test.com", UserRoleEnum.USER);

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(password, user.getPassword())).willReturn(true);
        given(jwtUtil.createToken(username)).willReturn("access_token");
        given(jwtUtil.createRefreshToken(username)).willReturn("refresh_token");
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        TokenResponseDto result = userService.login(username, password);

        assertEquals("access_token", result.getAccessToken());
        verify(valueOperations).set(eq("RT:" + username), eq("refresh_token"), anyLong(), any(TimeUnit.class));
    }

    @Test
    @DisplayName("로그아웃 성공 - Redis 토큰 삭제 확인")
    void logout_Success() {
        // given
        String username = "admin";

        // when
        userService.logout("dummy_access_token", username);

        // then
        verify(redisTemplate).delete("RT:" + username);
    }
}
