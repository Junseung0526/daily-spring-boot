package com.example.daily.service;

import com.example.daily.dto.UserRequestDto;
import com.example.daily.dto.UserResponseDto;
import com.example.daily.entity.User;
import com.example.daily.entity.UserRoleEnum;
import com.example.daily.exception.ErrorCode;
import com.example.daily.repository.UserRepository;
import com.example.daily.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgOTdqygdgHYiq";

    @Transactional
    public UserResponseDto createUser(UserRequestDto dto) {
        if (ur.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException(ErrorCode.DUPLICATE_USERNAME.getMessage());
        }

        UserRoleEnum role = UserRoleEnum.USER;
        if (dto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(dto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

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

    //단일 유저 조회 캐싱: 유저 정보가 바뀌기 전까지는 Redis에서 가져옴
    @Cacheable(value = "userProfile", key = "#userId", cacheManager = "cacheManager")
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

    //유저 삭제 시 관련 캐시도 함께 삭제
    @CacheEvict(value = "userProfile", key = "#userId")
    @Transactional
    public void deleteUser(Long userId) {
        User user = getUserById(userId);
        ur.delete(user);
    }
}
