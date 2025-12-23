package com.example.daily.service;

import com.example.daily.dto.TodoRequestDto;
import com.example.daily.dto.TodoResponseDto;
import com.example.daily.entity.Todo;
import com.example.daily.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository tr;

    //할 일 저장
    @Transactional
    public TodoResponseDto createTodo(TodoRequestDto dto) {
        Todo todo = Todo.builder()
                .title(dto.getTitle())
                .completed(dto.isCompleted())
                .build();

        Todo savedTodo = tr.save(todo);
        return new TodoResponseDto(savedTodo);
    }

    //전체 목록 조회
    public List<TodoResponseDto> getAllTodos() {
        return tr.findAll().stream()
                .map(TodoResponseDto::new)
                .collect(Collectors.toList());
    }

    //단건 조회
    public TodoResponseDto getTodoById(Long id) {
        Todo todo = tr.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 할 일이 존재하지 않습니다. id: " + id));
        return new TodoResponseDto(todo);
    }

    //할 일 수정
    @Transactional
    public TodoResponseDto updateTodo(Long id, TodoRequestDto dto) {
        Todo todo = getTodoEntity(id);

        todo.setTitle(dto.getTitle());
        todo.setCompleted(dto.isCompleted());

        return new TodoResponseDto(todo);
    }

    //할 일 삭제
    @Transactional
    public void deleteTodo(Long id) {
        Todo todo = getTodoEntity(id);
        tr.delete(todo);
    }

    // [내부 로직] 엔티티 조회 (중복 제거용)
    private Todo getTodoEntity(Long id) {
        return tr.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 할 일이 존재하지 않습니다. id: " + id));
    }

    //키워드로 검색 기능
    public List<TodoResponseDto> searchTodos(String keyword) {
        return tr.findByTitleContaining(keyword).stream()
                .map(TodoResponseDto::new)
                .collect(Collectors.toList());
    }
}
