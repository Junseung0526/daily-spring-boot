package com.example.daily.repository;

import com.example.daily.entity.Todo;

import java.util.List;

public interface TodoRepositoryCustom {
    List<Todo> searchTodos(String title, String tagName, Boolean completed, String username);
}
