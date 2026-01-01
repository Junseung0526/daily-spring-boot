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
            String encodedPassword = passwordEncoder.encode("1234");
            User admin = new User("admin", encodedPassword, "admin@test.com", UserRoleEnum.ADMIN);
            ur.save(admin);

            Tag springTag = tagRepository.save(new Tag("스프링"));
            Tag jpaTag = tagRepository.save(new Tag("JPA"));
            Tag studyTag = tagRepository.save(new Tag("공부"));

            // 3. 유저에게 할 일 할당 (태그 포함)
            if (tr.count() == 0) {
                // 첫 번째 할 일: 스프링, 공부 태그
                Todo todo1 = Todo.builder()
                        .title("스프링 부트 복습하기")
                        .completed(true)
                        .user(admin)
                        .tags(new ArrayList<>())
                        .build();
                todo1.addTag(springTag);
                todo1.addTag(studyTag);
                tr.save(todo1);

                // 두 번째 할 일: JPA, 공부 태그
                Todo todo2 = Todo.builder()
                        .title("JPA 쿼리 메서드 공부")
                        .completed(false)
                        .user(admin)
                        .tags(new ArrayList<>())
                        .build();
                todo2.addTag(jpaTag);
                todo2.addTag(studyTag);
                tr.save(todo2);

                // 세 번째 할 일: 태그 없음
                tr.save(Todo.builder()
                        .title("DTO 패턴 이해하기")
                        .completed(true)
                        .user(admin)
                        .tags(new ArrayList<>())
                        .build());
            }
        }
    }
}
