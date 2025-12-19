package com.example.daily.controller;

import com.example.daily.entity.Todo;
import com.example.daily.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService ts;

    //할 일 등록
    @PostMapping
    public Todo create(@RequestBody Todo todo) {
        return ts.createTodo(todo);
    }

    //할 일 조회
    @GetMapping
    public List<Todo> getAll() {
        return ts.getAllTodos();
    }

    //할 일 id별 조회
    @GetMapping("/{id}")
    public Todo getById(@PathVariable Long id) {
        return ts.getTodoById(id);
    }

    //할 일 업데이트
    @PutMapping("/{id}")
    public Todo update(@PathVariable Long id, @RequestBody Todo todo) {
        return ts.updateTodo(id, todo);
    }

    //할 일 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        ts.deleteTodo(id);
    }
}
