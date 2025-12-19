package com.example.daily.service;

import com.example.daily.dto.TodoRequestDto;
import com.example.daily.entity.Todo;
import com.example.daily.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {
    private final TodoRepository tr;

    //할 일 저장
    @Transactional
    public Todo createTodo(TodoRequestDto dto) {
        Todo todo = Todo.builder()
                .title(dto.getTitle())
                .completed(dto.isCompleted())
                .build();
        return tr.save(todo);
    }

    //전체 목록 조회
    public List<Todo> getAllTodos() {
        return tr.findAll();
    }

    //단건 조회
    public Todo getTodoById(Long id) {
        return tr.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 할 일이 존재하지 않음 ㅋ id: " + id));
    }

    //할 일 수정
    @Transactional
    public Todo updateTodo(Long id, TodoRequestDto dto) {
        Todo todo = getTodoById(id);
        todo.setTitle(dto.getTitle());
        todo.setCompleted(dto.isCompleted());
        return todo;
    }

    //할 일 삭제
    @Transactional
    public void deleteTodo(Long id) {
        Todo todo = getTodoById(id);
        tr.delete(todo);
    }

}
