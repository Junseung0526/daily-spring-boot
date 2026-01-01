package com.example.daily.controller;

import com.example.daily.dto.TodoRequestDto;
import com.example.daily.dto.TodoResponseDto;
import com.example.daily.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "할 일 관리 API", description = "투두 리스트의 생성, 조회, 수정, 삭제 및 상태 변경을 담당합니다.")
@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService ts;

    @Operation(summary = "할 일 등록", description = "제목을 입력받아 새로운 할 일을 저장합니다.")
    @PostMapping
    public TodoResponseDto create(
            @Valid @RequestBody TodoRequestDto dto,
            @AuthenticationPrincipal String username) {
        return ts.createTodo(dto, username);
    }

    @Operation(summary = "내 할 일 전체 조회", description = "로그인한 유저가 작성한 모든 할 일을 조회합니다.")
    @GetMapping
    public List<TodoResponseDto> getMyTodos(@AuthenticationPrincipal String username) {
        return ts.getAllTodosByUser(username);
    }

    @Operation(summary = "내 할 일 업데이트", description = "로그인한 유저가 작성한 할 일을 업데이트합니다.")
    @PutMapping("/{id}")
    public TodoResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody TodoRequestDto dto,
            @AuthenticationPrincipal String username) {
        return ts.updateTodo(id, dto, username);
    }

    @Operation(summary = "내 할 일 삭제", description = "로그인한 유저가 작성한 할 일을 삭제합니다.")
    @DeleteMapping("/{id}")
    public String delete(
            @PathVariable Long id,
            @AuthenticationPrincipal String username) {
        ts.deleteTodo(id, username);
        return "삭제 성공!";
    }

    @Operation(summary = "완료 상태 토글", description = "할 일의 완료 여부(true/false)를 반전시킵니다.")
    @PatchMapping("/{id}/completed")
    public TodoResponseDto toggleCompleted(
            @PathVariable Long id,
            @AuthenticationPrincipal String username) {
        return ts.toggleCompleted(id, username);
    }

    @Operation(summary = "할 일 페이징 조회", description = "최대 5개까지 표시되며 나머지는 페이징 처리되어 다음 페이지로 넘어갑니다.")
    @GetMapping("/paging")
    public Page<TodoResponseDto> getAllPaging(
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ts.getAllTodosPaging(pageable);
    }

    @Operation(summary = "할 일 단건 조회", description = "로그인한 유저가 작성한 할 일을 단건으로 조회합니다.")
    @GetMapping("/{id}")
    public TodoResponseDto getById(@PathVariable Long id) {
        return ts.getTodoById(id);
    }

    @Operation(summary = "태그 별 조회", description = "태그로 검색시 조회 기능을 제공합니다.")
    @GetMapping("/search/tag")
    public ResponseEntity<List<TodoResponseDto>> getTodosByTag(@RequestParam String tagName) {
        return ResponseEntity.ok(ts.getTodosByTagName(tagName));
    }

    @Operation(summary = "검색", description = "키워드 검색: GET /api/todos/search?keyword=공부\n" +
            "제목에 특정 키워드가 포함된 항목을 검색")
    @GetMapping("/search")
    public List<TodoResponseDto> search(@RequestParam(name = "keyword") String keyword) {
        return ts.searchTodos(keyword);
    }

    @Operation(summary = "필터링", description = "상태 필터링: GET /api/todos/filter?completed=true\n" +
            "완료(true) 또는 미완료(false) 항목만 골라봄")
    @GetMapping("/filter")
    public List<TodoResponseDto> filter(@RequestParam(name = "completed") boolean completed) {
        return ts.getTodoByStatus(completed);
    }
}
