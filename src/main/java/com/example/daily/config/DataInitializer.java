package com.example.daily.config;

import com.example.daily.entity.Todo;
import com.example.daily.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TodoRepository tr;

    @Override
    public void run(String... args) {
        // 기존 데이터가 없을 때만 실행 (중복 방지)
        if (tr.count() == 0) {
            tr.save(Todo.builder().title("스프링 부트 복습하기").completed(true).build());
            tr.save(Todo.builder().title("JPA 쿼리 메서드 공부").completed(false).build());
            tr.save(Todo.builder().title("DTO 패턴 이해하기").completed(true).build());
            tr.save(Todo.builder().title("포스트맨 테스트 시나리오 작성").completed(false).build());
            tr.save(Todo.builder().title("운동 가서 땀 흘리기").completed(false).build());
            tr.save(Todo.builder().title("자바 스트림 API 정복").completed(true).build());
            tr.save(Todo.builder().title("맛집 탐방 계획 세우기").completed(false).build());
            tr.save(Todo.builder().title("블로그에 오늘 공부한 내용 정리").completed(false).build());
            tr.save(Todo.builder().title("친구랑 약속 잡기").completed(true).build());
            tr.save(Todo.builder().title("내일 사냥 목표 설정").completed(false).build());
        }
    }
}
