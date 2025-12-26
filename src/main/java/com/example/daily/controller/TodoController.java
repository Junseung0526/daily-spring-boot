package com.example.daily.controller;

import com.example.daily.dto.TodoRequestDto;
import com.example.daily.dto.TodoResponseDto;
import com.example.daily.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService ts;

    // 할 일 등록
    @PostMapping
    public TodoResponseDto create(
            @Valid @RequestBody TodoRequestDto dto,
            @AuthenticationPrincipal String username) {
        return ts.createTodo(dto, username);
    }

    // 내 할 일 전체 조회
    @GetMapping
    public List<TodoResponseDto> getMyTodos(@AuthenticationPrincipal String username) {
        return ts.getAllTodosByUser(username);
    }

    // 할 일 업데이트
    @PutMapping("/{id}")
    public TodoResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody TodoRequestDto dto,
            @AuthenticationPrincipal String username) {
        return ts.updateTodo(id, dto, username);
    }

    // 할 일 삭제
    @DeleteMapping("/{id}")
    public String delete(
            @PathVariable Long id,
            @AuthenticationPrincipal String username) {
        ts.deleteTodo(id, username);
        return "삭제 성공!";
    }

    // 페이징 조회
    @GetMapping("/paging")
    public Page<TodoResponseDto> getAllPaging(
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ts.getAllTodosPaging(pageable);
    }

    // 단건 조회
    @GetMapping("/{id}")
    public TodoResponseDto getById(@PathVariable Long id) {
        return ts.getTodoById(id);
    }

    // --- 검색 및 필터링 ---
    @GetMapping("/search")
    public List<TodoResponseDto> search(@RequestParam(name = "keyword") String keyword) {
        return ts.searchTodos(keyword);
    }

    @GetMapping("/filter")
    public List<TodoResponseDto> filter(@RequestParam(name = "completed") boolean completed) {
        return ts.getTodoByStatus(completed);
    }
}
