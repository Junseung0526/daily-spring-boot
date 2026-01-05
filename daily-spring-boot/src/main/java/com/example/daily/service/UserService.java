package com.example.daily.service;

import com.example.daily.dto.TokenResponseDto;
import com.example.daily.dto.UserRequestDto;
import com.example.daily.dto.UserResponseDto;
import com.example.daily.entity.User;
import com.example.daily.entity.UserRoleEnum;
import com.example.daily.exception.ErrorCode;
import com.example.daily.repository.UserRepository;
import com.example.daily.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository ur;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

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

    @Transactional
    public TokenResponseDto login(String username, String password) {
        log.info("로그인 시도: username = {}", username);

        User user = ur.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage())
        );

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException(ErrorCode.INVALID_INPUT_VALUE.getMessage());
        }

        String accessToken = jwtUtil.createToken(user.getUsername());
        String refreshToken = jwtUtil.createRefreshToken(user.getUsername());

        redisTemplate.opsForValue().set(
                "RT:" + user.getUsername(),
                refreshToken,
                7, TimeUnit.DAYS
        );

        log.info("✅ 로그인 완료 및 Redis 저장 성공: {}", username);
        return new TokenResponseDto(accessToken, refreshToken);
    }

    //토큰 재발급 로직
    @Transactional
    public String reissue(String refreshToken) {
        //Refresh Token 검증 (Bearer 접두사 제거 로직 필요시 추가)
        String token = refreshToken.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        //유저 정보 추출
        String username = jwtUtil.getUserInfoFromToken(token).getSubject();

        //Redis에 저장된 토큰과 대조
        String savedToken = redisTemplate.opsForValue().get("RT:" + username);
        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token이 일치하지 않거나 만료되었습니다.");
        }

        //새로운 Access Token 발급
        return jwtUtil.createToken(username);
    }

    @Transactional
    public void logout(String accessToken, String username) {
        log.info("로그아웃 시도: Redis에서 Refresh Token 제거 중 (RT:{})", username);
        redisTemplate.delete("RT:" + username);

        log.info("✅ 로그아웃 성공: 유저 {}의 세션이 종료되었습니다.", username);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return ur.findById(userId).orElseThrow(
                () -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage())
        );
    }

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

    @CacheEvict(value = "userProfile", key = "#userId")
    @Transactional
    public void deleteUser(Long userId) {
        User user = getUserById(userId);
        redisTemplate.delete("RT:" + user.getUsername());
        ur.delete(user);
    }
}
