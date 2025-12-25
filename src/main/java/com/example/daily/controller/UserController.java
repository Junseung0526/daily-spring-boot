package com.example.daily.controller;

import com.example.daily.dto.UserRequestDto;
import com.example.daily.dto.UserResponseDto;
import com.example.daily.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService us;

    //회원가입
    @PostMapping("/signup")
    public UserResponseDto signup(@RequestBody UserRequestDto dto) {
        return us.createUser(dto);
    }

    //로그인
    @PostMapping("/login")
    public String login(@RequestBody UserRequestDto dto) {
        return us.login(dto.getUsername(), dto.getPassword());
    }

    //유저 전체 조회
    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return us.getAllUsers();
    }

    //유저 단건 조회
    @GetMapping("/{userId}")
    public UserResponseDto getUser(@PathVariable Long userId) {
        return us.getUserDtoById(userId);
    }

    //유저 삭제
    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        us.deleteUser(userId);
        return "유저와 관련된 모든 할 일이 삭제되었습니다. ID: " + userId;
    }
}
