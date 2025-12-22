package com.example.daily.dto;

import com.example.daily.entity.Todo;
import lombok.Getter;

@Getter
public class TodoResponseDto {
    private Long id;
    private String title;
    private boolean completed;

    public TodoResponseDto(Todo todo) {
        this.id = todo.getId();
        this.title = todo.getTitle();
        this.completed = todo.isCompleted();
    }
}
