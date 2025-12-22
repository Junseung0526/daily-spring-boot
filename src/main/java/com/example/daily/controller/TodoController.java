package com.example.daily.controller;

import com.example.daily.dto.TodoRequestDto;
import com.example.daily.dto.TodoResponseDto;
import com.example.daily.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService ts;

    //할 일 등록
    @PostMapping
    public TodoResponseDto create(@RequestBody TodoRequestDto dto) {
        return ts.createTodo(dto);
    }

    //전체 할 일 조회
    @GetMapping
    public List<TodoResponseDto> getAll() {
        return ts.getAllTodos();
    }

    //할 일 ID별 조회
    @GetMapping("/{id}")
    public TodoResponseDto getById(@PathVariable Long id) {
        return ts.getTodoById(id);
    }

    //할 일 업데이트
    @PutMapping("/{id}")
    public TodoResponseDto update(@PathVariable Long id, @RequestBody TodoRequestDto dto) {
        return ts.updateTodo(id, dto);
    }

    //할 일 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        ts.deleteTodo(id);
    }
}
