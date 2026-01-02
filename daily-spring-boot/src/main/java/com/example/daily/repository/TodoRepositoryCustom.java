package com.example.daily.repository;

import com.example.daily.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TodoRepositoryCustom {
    List<Todo> searchTodos(String title, String tagName, Boolean completed, String username);
    Page<Todo> searchTodos(String title, String tagName, Boolean completed, String username, Pageable pageable);
}
