package com.app.appuserservice.rest;

import com.app.appuserservice.dto.UserDTO;
import com.app.appuserservice.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UsersController {

    final Environment env;

    final UserService userService;

    public UsersController(final Environment env, final UserService userService) {
        this.env = env;
        this.userService = userService;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        return ResponseEntity.ok(Map.of("app-name", "users-ms on port: " + env.getProperty("local.server.port")));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.save(userDTO));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUsers(@NotBlank @PathVariable("userId")String userId) {
        return ResponseEntity.ok(userService.findByUserId(userId));
    }
/*
    @PostMapping
    public ResponseEntity<String> login(@Valid @RequestBody LoginDTO loginDTO) {
        var token = userService.login(loginDTO);
        return ResponseEntity.ok(token);
    }*/
}
