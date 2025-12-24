package com.example.daily.controller;

import com.example.daily.entity.User;
import com.example.daily.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService us;

    @PostMapping
    public User createUser(@RequestParam String username, @RequestParam String email) {
        return us.createUser(username, email);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return us.getAllUsers();
    }
}
