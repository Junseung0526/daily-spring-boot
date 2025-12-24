package com.example.daily.service;

import com.example.daily.entity.User;
import com.example.daily.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository ur;

    // 유저 생성
    @Transactional
    public User createUser(String username, String email) {
        User user = new User(username, email);
        return ur.save(user);
    }

    // 유저 단건 조회 (에러 처리 포함)
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return ur.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다.")
        );
    }

    // 유저 전체 조회
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return ur.findAll();
    }
}
