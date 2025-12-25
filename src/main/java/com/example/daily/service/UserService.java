package com.example.daily.service;

import com.example.daily.dto.UserRequestDto;
import com.example.daily.dto.UserResponseDto;
import com.example.daily.entity.User;
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

    //유저 생성
    @Transactional
    public UserResponseDto createUser(UserRequestDto dto) {
        // 1. 중복 유저 확인 (선택 사냥)
        if (ur.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // 3. 유저 저장
        User user = new User(dto.getUsername(), encodedPassword, dto.getEmail());
        return new UserResponseDto(ur.save(user));
    }

    @Transactional(readOnly = true)
    public String login(String username, String password) {
        User user = ur.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("등록된 사용자가 아닙니다.")
        );

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return jwtUtil.createToken(user.getUsername());
    }

    //유저 단건 조회 (내부 로직용 - Entity 반환)
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return ur.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다.")
        );
    }

    //유저 단건 조회 (외부 API용 - DTO 반환)
    @Transactional(readOnly = true)
    public UserResponseDto getUserDtoById(Long userId) {
        User user = getUserById(userId);
        return new UserResponseDto(user);
    }


    //유저 전체 조회
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return ur.findAll().stream()
                .map(UserResponseDto::new) // Entity 리스트를 DTO 리스트로 변환
                .collect(Collectors.toList());
    }

    //유저 삭제 (Cascade 설정에 의해 작성한 Todo도 함께 삭제됨)
    @Transactional
    public void deleteUser(Long userId) {
        User user = getUserById(userId);
        ur.delete(user);
    }
}
