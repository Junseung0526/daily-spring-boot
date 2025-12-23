package com.example.daily.repository;

import com.example.daily.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    // 인터페이스만 선언해도 기본적인 Save, Find, Delete 메서드가 자동 생성

    List<Todo> findByTitleContaining(String keyword);
    List<Todo> findByCompleted(Boolean completed);
    List<Todo> findByTitleContainingAndCompleted(String keyword, boolean completed);
}
