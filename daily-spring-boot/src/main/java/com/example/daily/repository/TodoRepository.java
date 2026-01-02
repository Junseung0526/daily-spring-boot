package com.example.daily.repository;

import com.example.daily.entity.Todo;
import com.example.daily.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long>, TodoRepositoryCustom {
    // 인터페이스만 선언해도 기본적인 Save, Find, Delete 메서드가 자동 생성

    List<Todo> findByTitleContaining(String keyword);

    List<Todo> findByCompleted(Boolean completed);

    List<Todo> findByTitleContainingAndCompleted(String keyword, boolean completed);

    List<Todo> findAllByUser(User user);

    @Query("SELECT DISTINCT t FROM Todo t " +
            "JOIN FETCH t.tags tg " +
            "WHERE tg.name = :tagName")
    List<Todo> findAllByTagName(@Param("tagName") String tagName);
}
