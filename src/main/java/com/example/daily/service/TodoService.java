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

    // 새 글 작성 시 기존 캐시 삭제
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

        //태그 추가
        if (dto.getTagNames() != null && !dto.getTagNames().isEmpty()) {
            for (String tagName : dto.getTagNames()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));

                todo.addTag(tag);
            }
        }

        Todo savedTodo = tr.save(todo);
        return new TodoResponseDto(savedTodo);
    }

    // 캐시 적용
    @Cacheable(value = "todoList", key = "#username", cacheManager = "cacheManager")
    public List<TodoResponseDto> getAllTodosByUser(String username) {
        User user = getUserByUsername(username);

        return tr.findAllByUser(user).stream()
                .map(TodoResponseDto::new)
                .collect(Collectors.toList());
    }

    // 수정 시 캐시 삭제
    @CacheEvict(value = "todoList", key = "#username")
    @Transactional
    public TodoResponseDto updateTodo(Long id, TodoRequestDto dto, String username) {
        Todo todo = getTodoEntity(id);
        User user = getUserByUsername(username);

        if (!todo.getUser().getUsername().equals(username) && user.getRole() != UserRoleEnum.ADMIN) {
            throw new IllegalArgumentException(ErrorCode.UNAUTHORIZED_UPDATE.getMessage());
        }

        todo.setTitle(dto.getTitle());
        todo.setCompleted(dto.isCompleted());

        //태그 수정
        if (dto.getTagNames() != null) {
            //기존 태그 삭제
            todo.getTags().clear();

            //태그 추가
            for (String tagName : dto.getTagNames()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));
                todo.addTag(tag);
            }
        }

        return new TodoResponseDto(todo);
    }

    // 삭제 시 캐시 삭제
    @CacheEvict(value = "todoList", key = "#username")
    @Transactional
    public void deleteTodo(Long id, String username) {
        Todo todo = getTodoEntity(id);
        User user = getUserByUsername(username);

        if (!todo.getUser().getUsername().equals(username) && user.getRole() != UserRoleEnum.ADMIN) {
            throw new IllegalArgumentException(ErrorCode.UNAUTHORIZED_DELETE.getMessage());
        }

        tr.delete(todo);
    }

    // 상태 변경 시 캐시 삭제
    @CacheEvict(value = "todoList", key = "#username")
    @Transactional
    public TodoResponseDto toggleCompleted(Long id, String username) {
        Todo todo = getTodoEntity(id);
        User user = getUserByUsername(username);

        if (!todo.getUser().getUsername().equals(username) && user.getRole() != UserRoleEnum.ADMIN) {
            throw new IllegalArgumentException(ErrorCode.UNAUTHORIZED_UPDATE.getMessage());
        }

        todo.setCompleted(!todo.isCompleted());

        return new TodoResponseDto(todo);
    }

    @Transactional(readOnly = true)
    public List<TodoResponseDto> getTodosByTagName(String tagName) {
        List<Todo> todos = tr.findAllByTagName(tagName);
        return todos.stream().map(TodoResponseDto::new).toList();
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

    private User getUserByUsername(String username) {
        return ur.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage()));
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
