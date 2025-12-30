package com.example.daily.service;

import com.example.daily.dto.UserRequestDto;
import com.example.daily.dto.UserResponseDto;
import com.example.daily.entity.User;
import com.example.daily.entity.UserRoleEnum;
import com.example.daily.exception.ErrorCode;
import com.example.daily.repository.UserRepository;
import com.example.daily.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository ur;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    //이건 다른데서 사용시 환경 변수로 적용시켜야 함
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgOTdqygdgHYiq";

    @Transactional
    public UserResponseDto createUser(UserRequestDto dto) {
        // 중복 확인
        if (ur.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException(ErrorCode.DUPLICATE_USERNAME.getMessage());
        }

        // 권한 확인 및 결정
        UserRoleEnum role = UserRoleEnum.USER;
        if (dto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(dto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        // 비밀번호 암호화 및 유저 생성
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        User user = new User(dto.getUsername(), encodedPassword, dto.getEmail(), role);
        return new UserResponseDto(ur.save(user));
    }

    @Transactional(readOnly = true)
    public String login(String username, String password) {
        User user = ur.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage())
        );

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException(ErrorCode.INVALID_INPUT_VALUE.getMessage());
        }

        return jwtUtil.createToken(user.getUsername());
    }

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return ur.findById(userId).orElseThrow(
                () -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage())
        );
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserDtoById(Long userId) {
        User user = getUserById(userId);
        return new UserResponseDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return ur.findAll().stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = getUserById(userId);
        ur.delete(user);
    }
}
