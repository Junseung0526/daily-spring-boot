package com.example.daily.service;

import com.example.daily.entity.Todo;
import com.example.daily.entity.User;
import com.example.daily.entity.UserRoleEnum;
import com.example.daily.exception.ErrorCode;
import com.example.daily.exception.RestApiException;
import com.example.daily.repository.TodoRepository;
import com.example.daily.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock TodoRepository todoRepository;
    @Mock UserRepository userRepository;
    @InjectMocks TodoService todoService;

    @Test
    @DisplayName("삭제 실패 - 작성자가 아닌 일반 유저가 삭제 시도 시 예외 발생")
    void deleteTodo_Fail_Forbidden() {
        Long todoId = 1L;
        String intruderName = "intruder";
        User owner = new User("owner", "pass", "owner@test.com", UserRoleEnum.USER);
        User intruder = new User(intruderName, "pass", "intruder@test.com", UserRoleEnum.USER);

        Todo todo = Todo.builder().id(todoId).user(owner).build();

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(userRepository.findByUsername(intruderName)).willReturn(Optional.of(intruder));

        RestApiException exception = assertThrows(RestApiException.class, () ->
                todoService.deleteTodo(todoId, intruderName)
        );

        assertEquals(ErrorCode.UNAUTHORIZED_DELETE, exception.getErrorCode());
    }
}
