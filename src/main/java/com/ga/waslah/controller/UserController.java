package com.ga.waslah.controller;

import com.ga.waslah.dto.RegisterRequest;
import com.ga.waslah.dto.UserResponseDTO;
import com.ga.waslah.model.User;
import com.ga.waslah.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        User user = userService.register(request);

        UserResponseDTO response = new UserResponseDTO(
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/test")
    public String test() {
        return "JWT works!";
    }
}