package com.example.daily.service;

import com.example.daily.dto.TodoRequestDto;
import com.example.daily.dto.TodoResponseDto;
import com.example.daily.entity.Tag;
import com.example.daily.entity.Todo;
import com.example.daily.entity.User;
import com.example.daily.entity.UserRoleEnum;
import com.example.daily.exception.ErrorCode;
import com.example.daily.repository.TagRepository;
import com.example.daily.repository.TodoRepository;
import com.example.daily.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository tr;
    private final UserRepository ur;
    private final TagRepository tagRepository;

    @CacheEvict(value = "todoList", key = "#username")
    @Transactional
    public TodoResponseDto createTodo(TodoRequestDto dto, String username) {
        User user = getUserByUsername(username);

        Todo todo = Todo.builder()
                .title(dto.getTitle())
                .completed(dto.isCompleted())
                .tags(new ArrayList<>())
                .build();
        todo.setUser(user);

        processTags(dto.getTagNames(), todo);

        Todo savedTodo = tr.save(todo);
        return new TodoResponseDto(savedTodo);
    }

    @Cacheable(value = "todoList", key = "#username", cacheManager = "cacheManager")
    public List<TodoResponseDto> getAllTodosByUser(String username) {
        User user = getUserByUsername(username);
        return tr.findAllByUser(user).stream()
                .map(TodoResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 3. 동적 검색 (QueryDSL 활용)
     * 검색 조건: 제목(키워드), 태그명, 완료 여부
     * 주의: 검색 결과는 조건이 다양하므로 캐시를 적용하지 않는 것이 일반적입니다.
     */
    public List<TodoResponseDto> searchTodosDynamic(String title, String tagName, Boolean completed, String username) {
        return tr.searchTodos(title, tagName, completed, username).stream()
                .map(TodoResponseDto::new)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "todoList", key = "#username")
    @Transactional
    public TodoResponseDto updateTodo(Long id, TodoRequestDto dto, String username) {
        Todo todo = getTodoEntity(id);
        checkAuthority(todo, username);

        todo.setTitle(dto.getTitle());
        todo.setCompleted(dto.isCompleted());

        if (dto.getTagNames() != null) {
            todo.getTags().clear();
            processTags(dto.getTagNames(), todo);
        }

        return new TodoResponseDto(todo);
    }

    @CacheEvict(value = "todoList", key = "#username")
    @Transactional
    public void deleteTodo(Long id, String username) {
        Todo todo = getTodoEntity(id);
        checkAuthority(todo, username);
        tr.delete(todo);
    }

    @CacheEvict(value = "todoList", key = "#username")
    @Transactional
    public TodoResponseDto toggleCompleted(Long id, String username) {
        Todo todo = getTodoEntity(id);
        checkAuthority(todo, username);
        todo.setCompleted(!todo.isCompleted());
        return new TodoResponseDto(todo);
    }

    private void processTags(List<String> tagNames, Todo todo) {
        if (tagNames != null && !tagNames.isEmpty()) {
            for (String tagName : tagNames) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));
                todo.addTag(tag);
            }
        }
    }

    private void checkAuthority(Todo todo, String username) {
        User user = getUserByUsername(username);
        if (!todo.getUser().getUsername().equals(username) && user.getRole() != UserRoleEnum.ADMIN) {
            throw new IllegalArgumentException(ErrorCode.UNAUTHORIZED_UPDATE.getMessage());
        }
    }

    private Todo getTodoEntity(Long id) {
        return tr.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.TODO_NOT_FOUND.getMessage()));
    }

    private User getUserByUsername(String username) {
        return ur.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage()));
    }

    public TodoResponseDto getTodoById(Long id) {
        return new TodoResponseDto(getTodoEntity(id));
    }

    public Page<TodoResponseDto> getAllTodosPaging(Pageable pageable) {
        return tr.findAll(pageable).map(TodoResponseDto::new);
    }
}
