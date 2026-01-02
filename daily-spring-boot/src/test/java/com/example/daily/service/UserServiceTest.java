package com.example.daily.service;

import com.example.daily.dto.UserRequestDto;
import com.example.daily.dto.UserResponseDto;
import com.example.daily.entity.User;
import com.example.daily.entity.UserRoleEnum;
import com.example.daily.exception.ErrorCode;
import com.example.daily.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Nested
    @DisplayName("회원가입 테스트")
    class CreateUser {

        @Test
        @DisplayName("성공 - 일반 유저로 가입")
        void createUser_Success_User() {
            UserRequestDto requestDto = new UserRequestDto();
            ReflectionTestUtils.setField(requestDto, "username", "newbie");
            ReflectionTestUtils.setField(requestDto, "password", "password123");
            ReflectionTestUtils.setField(requestDto, "email", "newbie@test.com");
            ReflectionTestUtils.setField(requestDto, "admin", false);

            given(userRepository.existsByUsername("newbie")).willReturn(false);
            given(passwordEncoder.encode(any())).willReturn("encoded_password");

            User savedUser = new User("newbie", "encoded_password", "newbie@test.com", UserRoleEnum.USER);
            given(userRepository.save(any(User.class))).willReturn(savedUser);

            UserResponseDto result = userService.createUser(requestDto);

            assertEquals("newbie", result.getUsername());
        }

        @Test
        @DisplayName("실패 - 이미 존재하는 아이디로 가입 시도")
        void createUser_Fail_DuplicateUsername() {
            UserRequestDto requestDto = new UserRequestDto();
            ReflectionTestUtils.setField(requestDto, "username", "existingUser");

            given(userRepository.existsByUsername("existingUser")).willReturn(true);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(requestDto)
            );

            assertEquals(ErrorCode.DUPLICATE_USERNAME.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("실패 - 관리자 토큰이 틀리면 가입할 수 없다")
        void createUser_Fail_WrongAdminToken() {
            UserRequestDto requestDto = new UserRequestDto();
            ReflectionTestUtils.setField(requestDto, "username", "adminTry");
            ReflectionTestUtils.setField(requestDto, "admin", true);
            ReflectionTestUtils.setField(requestDto, "adminToken", "WRONG_TOKEN");

            given(userRepository.existsByUsername("adminTry")).willReturn(false);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(requestDto)
            );

            assertEquals("관리자 암호가 틀려 등록이 불가능합니다.", exception.getMessage());
        }
    }
}
