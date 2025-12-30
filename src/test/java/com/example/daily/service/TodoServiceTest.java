package com.example.daily.service;

import com.example.daily.dto.TodoRequestDto;
import com.example.daily.dto.TodoResponseDto;
import com.example.daily.entity.Todo;
import com.example.daily.entity.User;
import com.example.daily.entity.UserRoleEnum;
import com.example.daily.exception.ErrorCode;
import com.example.daily.repository.TodoRepository;
import com.example.daily.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    TodoRepository todoRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    TodoService todoService;

    @Nested
    @DisplayName("할 일 수정 테스트")
    class UpdateTodo {

        @Test
        @DisplayName("성공 - 작성자 본인은 본인의 할 일을 수정할 수 있다")
        void updateTodo_Success_Owner() {
            // given
            Long todoId = 1L;
            String username = "userA";
            User user = new User(username, "pass", "userA@test.com", UserRoleEnum.USER);
            Todo todo = Todo.builder().title("원래 제목").user(user).build();
            TodoRequestDto requestDto = new TodoRequestDto("수정된 제목", true);

            given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
            given(userRepository.findByUsername(username)).willReturn(Optional.of(user));

            // when
            TodoResponseDto result = todoService.updateTodo(todoId, requestDto, username);

            // then
            assertEquals("수정된 제목", result.getTitle());
            assertTrue(todo.isCompleted());
        }

        @Test
        @DisplayName("성공 - 관리자는 다른 유저의 할 일을 수정할 수 있다")
        void updateTodo_Success_Admin() {
            // given
            Long todoId = 1L;
            String adminName = "adminUser";
            User owner = new User("userA", "pass", "userA@test.com", UserRoleEnum.USER);
            User admin = new User(adminName, "pass", "admin@test.com", UserRoleEnum.ADMIN);
            Todo todo = Todo.builder().title("원래 제목").user(owner).build();
            TodoRequestDto requestDto = new TodoRequestDto("관리자 수정", true);

            given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
            given(userRepository.findByUsername(adminName)).willReturn(Optional.of(admin));

            // when
            TodoResponseDto result = todoService.updateTodo(todoId, requestDto, adminName);

            // then
            assertEquals("관리자 수정", result.getTitle());
        }

        @Test
        @DisplayName("실패 - 본인이 아니고 관리자도 아니면 예외가 발생한다")
        void updateTodo_Fail_Unauthorized() {
            // given
            Long todoId = 1L;
            String intruderName = "intruder";
            User owner = new User("userA", "pass", "userA@test.com", UserRoleEnum.USER);
            User intruder = new User(intruderName, "pass", "intruder@test.com", UserRoleEnum.USER);
            Todo todo = Todo.builder().title("원래 제목").user(owner).build();
            TodoRequestDto requestDto = new TodoRequestDto("몰래 수정", true);

            given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
            given(userRepository.findByUsername(intruderName)).willReturn(Optional.of(intruder));

            // when & then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                todoService.updateTodo(todoId, requestDto, intruderName)
            );

            assertEquals(ErrorCode.UNAUTHORIZED_UPDATE.getMessage(), exception.getMessage());
        }
    }
}
