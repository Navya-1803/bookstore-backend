package com.bookstore.user.controller;

import com.bookstore.user.dto.RegisterRequest;
import com.bookstore.user.dto.UserResponse;
import com.bookstore.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.bookstore.user.dto.LoginRequest;
import com.bookstore.user.dto.LoginResponse;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(
            @Valid @RequestBody RegisterRequest request) {

        return userService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {

        return userService.login(request);
    }
}