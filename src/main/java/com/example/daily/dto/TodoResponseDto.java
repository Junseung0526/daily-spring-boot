package com.example.daily.dto;

import com.example.daily.entity.Tag;
import com.example.daily.entity.Todo;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TodoResponseDto {
    private Long id;
    private String title;
    private boolean completed;
    private List<String> tagList;

    public TodoResponseDto(Todo todo) {
        this.id = todo.getId();
        this.title = todo.getTitle();
        this.completed = todo.isCompleted();
        this.tagList = todo.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }
}
