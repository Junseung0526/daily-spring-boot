package com.example.daily.config;

import com.example.daily.entity.Tag;
import com.example.daily.entity.Todo;
import com.example.daily.entity.User;
import com.example.daily.entity.UserRoleEnum;
import com.example.daily.repository.TagRepository;
import com.example.daily.repository.TodoRepository;
import com.example.daily.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TodoRepository tr;
    private final UserRepository ur;
    private final TagRepository tagRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (ur.count() == 0) {
            // 1. 테스트 유저 생성
            String encodedPassword = passwordEncoder.encode("1234");
            User admin = new User("admin", encodedPassword, "admin@test.com", UserRoleEnum.ADMIN);
            ur.save(admin);

            // 2. 태그 생성
            Tag springTag = tagRepository.save(new Tag("스프링"));
            Tag jpaTag = tagRepository.save(new Tag("JPA"));
            Tag studyTag = tagRepository.save(new Tag("공부"));
            Tag dailyTag = tagRepository.save(new Tag("일상"));

            // 3. 할 일 대량 생성 (페이징 테스트용)
            if (tr.count() == 0) {
                // 기존 수동 생성 데이터
                saveTodo(admin, "스프링 부트 복습하기", true, springTag, studyTag);
                saveTodo(admin, "JPA 쿼리 메서드 공부", false, jpaTag, studyTag);
                saveTodo(admin, "DTO 패턴 이해하기", true);

                // 반복문을 이용한 20개 추가 생성
                for (int i = 1; i <= 20; i++) {
                    String title = "페이징 테스트용 할 일 " + i;
                    boolean completed = i % 2 == 0; // 짝수는 완료, 홀수는 미완료

                    // 태그도 번갈아가며 넣어줌
                    if (i % 3 == 0) {
                        saveTodo(admin, title, completed, springTag, dailyTag);
                    } else if (i % 3 == 1) {
                        saveTodo(admin, title, completed, jpaTag);
                    } else {
                        saveTodo(admin, title, completed, studyTag);
                    }
                }
            }
        }
    }

    // 데이터 생성을 편하게 하기 위한 헬퍼 메서드
    private void saveTodo(User user, String title, boolean completed, Tag... tags) {
        Todo todo = Todo.builder()
                .title(title)
                .completed(completed)
                .user(user)
                .tags(new ArrayList<>())
                .build();

        for (Tag tag : tags) {
            todo.addTag(tag);
        }
        tr.save(todo);
    }
}
