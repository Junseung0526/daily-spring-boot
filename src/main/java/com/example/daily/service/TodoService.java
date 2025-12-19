package com.example.service;

import com.example.entity.Todo;
import com.example.repository.TodoRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {
    private final TodoRepository tr;

    //할 일 저장
    @Transactional
    public Todo createTodo(Todo todo) {
        return tr.save(todo);
    }

    //전체 목록 조회
    @Transactional
    public List<Todo> getAllTodos() {
        return tr.findAll();
    }

    //단건 조회
    @Transactional
    public Todo getTodoById(Long id) {
        return tr.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 할 일이 존재하지 않음 ㅋ"));
    }

    //할 일 수정
    @Transactional
    public Todo updateTodo(Long id, Todo requestTodo) {
        Todo todo = getTodoById(id);
        todo.setTitle(requestTodo.getTitle());
        todo.setCompleted(requestTodo.isCompleted());
        return todo;
    }

    //할 일 삭제
    @Transactional
    public void deleteTodo(Long id) {
        Todo todo = getTodoById(id);
        tr.delete(todo);
    }

}
