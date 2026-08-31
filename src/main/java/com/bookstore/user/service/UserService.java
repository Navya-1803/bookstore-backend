package com.bookstore.user.service;

import com.bookstore.security.JwtUtil;
import com.bookstore.user.dto.LoginRequest;
import com.bookstore.user.dto.LoginResponse;
import com.bookstore.user.dto.RegisterRequest;
import com.bookstore.user.dto.UserResponse;
import com.bookstore.user.entity.Role;
import com.bookstore.user.entity.User;
import com.bookstore.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(
            UserRepository userRepository,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtUtil = jwtUtil;
    }

    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getName(),
                request.getEmail(),
                encodedPassword,
                Role.USER
        );

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password")
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );

        return new LoginResponse(token, userResponse);
    }
}