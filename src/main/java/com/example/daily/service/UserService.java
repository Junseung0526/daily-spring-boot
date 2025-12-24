package com.example.daily.service;

import com.example.daily.dto.UserResponseDto;
import com.example.daily.entity.User;
import com.example.daily.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository ur;

    //유저 생성
    @Transactional
    public UserResponseDto createUser(String username, String email) {
        User user = new User(username, email);
        User savedUser = ur.save(user);
        return new UserResponseDto(savedUser);
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
