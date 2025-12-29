package com.example.daily.service;

import com.example.daily.dto.TodoRequestDto;
import com.example.daily.dto.TodoResponseDto;
import com.example.daily.entity.Todo;
import com.example.daily.entity.User;
import com.example.daily.exception.ErrorCode;
import com.example.daily.repository.TodoRepository;
import com.example.daily.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository tr;
    private final UserRepository ur;

    @Transactional
    public TodoResponseDto createTodo(TodoRequestDto dto, String username) {
        User user = ur.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage()));

        Todo todo = Todo.builder()
                .title(dto.getTitle())
                .completed(dto.isCompleted())
                .build();
        todo.setUser(user);

        return new TodoResponseDto(tr.save(todo));
    }

    public List<TodoResponseDto> getAllTodosByUser(String username) {
        User user = ur.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage()));

        return tr.findAllByUser(user).stream()
                .map(TodoResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public TodoResponseDto updateTodo(Long id, TodoRequestDto dto, String username) {
        Todo todo = getTodoEntity(id);

        if (!todo.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException(ErrorCode.UNAUTHORIZED_UPDATE.getMessage());
        }

        todo.setTitle(dto.getTitle());
        todo.setCompleted(dto.isCompleted());

        return new TodoResponseDto(todo);
    }

    @Transactional
    public void deleteTodo(Long id, String username) {
        Todo todo = getTodoEntity(id);

        if (!todo.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException(ErrorCode.UNAUTHORIZED_DELETE.getMessage());
        }

        tr.delete(todo);
    }

    @Transactional
    public TodoResponseDto toggleCompleted(Long id, String username) {
        Todo todo = getTodoEntity(id);

        if (!todo.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException(ErrorCode.UNAUTHORIZED_UPDATE.getMessage());
        }

        todo.setCompleted(!todo.isCompleted());

        return new TodoResponseDto(todo);
    }

    public TodoResponseDto getTodoById(Long id) {
        return new TodoResponseDto(getTodoEntity(id));
    }

    public Page<TodoResponseDto> getAllTodosPaging(Pageable pageable) {
        return tr.findAll(pageable).map(TodoResponseDto::new);
    }

    private Todo getTodoEntity(Long id) {
        return tr.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.TODO_NOT_FOUND.getMessage()));
    }

    public List<TodoResponseDto> searchTodos(String keyword) {
        return tr.findByTitleContaining(keyword).stream()
                .map(TodoResponseDto::new).toList();
    }

    public List<TodoResponseDto> getTodoByStatus(boolean completed) {
        return tr.findByCompleted(completed).stream()
                .map(TodoResponseDto::new).toList();
    }

    public List<TodoResponseDto> getTodoByKeywordAndStatus(String keyword, boolean completed) {
        return tr.findByTitleContainingAndCompleted(keyword, completed).stream()
                .map(TodoResponseDto::new).toList();
    }
}
