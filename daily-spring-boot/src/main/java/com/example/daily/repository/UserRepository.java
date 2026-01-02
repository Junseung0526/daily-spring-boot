package com.example.daily.repository;

import com.example.daily.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 로그인 시 아이디로 유저 정보를 가져오기 위함
    Optional<User> findByUsername(String username);

    // 회원가입 시 이미 존재하는 아이디인지 확인
    boolean existsByUsername(String username);
}
