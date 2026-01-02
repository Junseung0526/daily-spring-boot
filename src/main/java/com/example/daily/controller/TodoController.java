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

    @Operation(summary = "할 일 등록", description = "제목과 태그를 입력받아 새로운 할 일을 저장합니다.")
    @PostMapping
    public TodoResponseDto create(
            @Valid @RequestBody TodoRequestDto dto,
            @AuthenticationPrincipal String username) {
        return ts.createTodo(dto, username);
    }

    @Operation(summary = "내 할 일 전체 조회", description = "로그인한 유저가 작성한 모든 할 일을 조회합니다. (Redis 캐싱 적용)")
    @GetMapping
    public List<TodoResponseDto> getMyTodos(@AuthenticationPrincipal String username) {
        return ts.getAllTodosByUser(username);
    }

    @Operation(summary = "내 할 일 업데이트", description = "할 일의 제목, 완료 여부, 태그 목록을 수정합니다.")
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

    @Operation(summary = "완료 상태 토글", description = "할 일의 완료 여부를 반전시킵니다.")
    @PatchMapping("/{id}/completed")
    public TodoResponseDto toggleCompleted(
            @PathVariable Long id,
            @AuthenticationPrincipal String username) {
        return ts.toggleCompleted(id, username);
    }

    @Operation(summary = "통합 동적 검색", description = "제목, 태그명, 완료 여부를 조합하여 내 할 일 목록 내에서 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<Page<TodoResponseDto>> search(
                                                         @RequestParam(required = false) String title,
                                                         @RequestParam(required = false) String tagName,
                                                         @RequestParam(required = false) Boolean completed,
                                                         @AuthenticationPrincipal String username,
                                                         @PageableDefault(size = 5) Pageable pageable) {

        return ResponseEntity.ok(ts.searchTodosDynamic(title, tagName, completed, username, pageable));
    }

    @Operation(summary = "할 일 페이징 조회", description = "페이지 단위로 할 일을 조회합니다.")
    @GetMapping("/paging")
    public Page<TodoResponseDto> getAllPaging(
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ts.getAllTodosPaging(pageable);
    }

    @Operation(summary = "할 일 단건 조회")
    @GetMapping("/{id}")
    public TodoResponseDto getById(@PathVariable Long id) {
        return ts.getTodoById(id);
    }
}
