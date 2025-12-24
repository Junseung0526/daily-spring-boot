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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService ts;

    //할 일 등록
    @PostMapping("/user/{userId}")
    public TodoResponseDto create(
            @Valid @RequestBody TodoRequestDto dto,
            @PathVariable Long userId) {

        return ts.createTodo(dto, userId);
    }

    //전체 할 일 조회 / 페이지별 5개 제한
    @GetMapping("/paging")
    public Page<TodoResponseDto> getAllPaging(
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ts.getAllTodosPaging(pageable);
    }

    //할 일 ID별 조회
    @GetMapping("/{id}")
    public TodoResponseDto getById(@PathVariable Long id) {
        return ts.getTodoById(id);
    }

    //User 별 목록 조회
    @GetMapping("/user/{userId}")
    public List<TodoResponseDto> getAllByUser(@PathVariable Long userId) {
        return ts.getAllTodosByUser(userId);
    }

    //할 일 업데이트
    @PutMapping("/{id}")
    public TodoResponseDto update(@PathVariable Long id, @Valid @RequestBody TodoRequestDto dto) {
        return ts.updateTodo(id, dto);
    }

    //할 일 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        ts.deleteTodo(id);
    }

    //키워드 검색
    @GetMapping("/search")
    public List<TodoResponseDto> search(@RequestParam(name = "keyword") String keyword) {
        return ts.searchTodos(keyword);
    }

    //완료 여부 필터링
    @GetMapping("/filter")
    public List<TodoResponseDto> filter(@RequestParam(name = "completed") boolean completed) {
        return ts.getTodoByStatus(completed);
    }

    //키워드 검색 & 완료 여부 필터링 같이함
    @GetMapping("/search/complex")
    public List<TodoResponseDto> complexSearch(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "completed") boolean completed) {
        return ts.getTodoByKeywordAndStatus(keyword, completed);
    }
}
