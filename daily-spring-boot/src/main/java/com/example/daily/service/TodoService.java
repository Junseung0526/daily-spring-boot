package com.example.daily.service;

import com.example.daily.dto.TodoRequestDto;
import com.example.daily.dto.TodoResponseDto;
import com.example.daily.entity.Tag;
import com.example.daily.entity.Todo;
import com.example.daily.entity.User;
import com.example.daily.entity.UserRoleEnum;
import com.example.daily.exception.ErrorCode;
import com.example.daily.exception.RestApiException; // 💡 커스텀 예외 추가
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
    private final WeatherService weatherService;

    @CacheEvict(value = "todoList", key = "#username")
    @Transactional
    public TodoResponseDto createTodo(TodoRequestDto dto, String username) {
        User user = getUserByUsername(username);

        String currentWeather = weatherService.getTodayWeather();

        Todo todo = Todo.builder()
                .title(dto.getTitle())
                .completed(dto.isCompleted())
                .weather(currentWeather)
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

    public Page<TodoResponseDto> searchTodosDynamic(String title, String tagName, Boolean completed, String username, Pageable pageable) {
        return tr.searchTodos(title, tagName, completed, username, pageable)
                .map(TodoResponseDto::new);
    }

    @CacheEvict(value = "todoList", key = "#username")
    @Transactional
    public TodoResponseDto updateTodo(Long id, TodoRequestDto dto, String username) {
        Todo todo = getTodoEntity(id);
        checkAuthority(todo, username, "UPDATE"); // 💡 권한 체크 개선

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
        checkAuthority(todo, username, "DELETE");
        tr.delete(todo);
    }

    @CacheEvict(value = "todoList", key = "#username")
    @Transactional
    public TodoResponseDto toggleCompleted(Long id, String username) {
        Todo todo = getTodoEntity(id);
        checkAuthority(todo, username, "UPDATE");
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

    // 💡 RestApiException을 사용하여 예외 처리 일관성 확보
    private void checkAuthority(Todo todo, String username, String type) {
        User user = getUserByUsername(username);
        if (!todo.getUser().getUsername().equals(username) && user.getRole() != UserRoleEnum.ADMIN) {
            if ("DELETE".equals(type)) {
                throw new RestApiException(ErrorCode.UNAUTHORIZED_DELETE);
            }
            throw new RestApiException(ErrorCode.UNAUTHORIZED_UPDATE);
        }
    }

    private Todo getTodoEntity(Long id) {
        return tr.findById(id)
                .orElseThrow(() -> new RestApiException(ErrorCode.TODO_NOT_FOUND));
    }

    private User getUserByUsername(String username) {
        return ur.findByUsername(username)
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));
    }

    public TodoResponseDto getTodoById(Long id) {
        return new TodoResponseDto(getTodoEntity(id));
    }

    public Page<TodoResponseDto> getAllTodosPaging(Pageable pageable) {
        return tr.findAll(pageable).map(TodoResponseDto::new);
    }
}
