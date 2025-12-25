package com.example.daily.config;

import com.example.daily.entity.Todo;
import com.example.daily.entity.User;
import com.example.daily.repository.TodoRepository;
import com.example.daily.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TodoRepository tr;
    private final UserRepository ur;
    private final PasswordEncoder passwordEncoder; // 💡 비번 암호화를 위해 주입

    @Override
    public void run(String... args) {
        if (ur.count() == 0) {
            // 1. 테스트용 유저 생성
            String encodedPassword = passwordEncoder.encode("1234");
            User admin = new User("admin", encodedPassword, "admin@test.com");
            ur.save(admin);

            // 2. 유저에게 할 일 할당 및 저장
            if (tr.count() == 0) {
                // Todo 생성 시 user를 반드시 세팅해줍니다.
                tr.save(Todo.builder().title("스프링 부트 복습하기").completed(true).user(admin).build());
                tr.save(Todo.builder().title("JPA 쿼리 메서드 공부").completed(false).user(admin).build());
                tr.save(Todo.builder().title("DTO 패턴 이해하기").completed(true).user(admin).build());
                tr.save(Todo.builder().title("내일 사냥 목표 설정").completed(false).user(admin).build());
            }
        }
    }
}
