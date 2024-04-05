package com.app.appuserservice.rest;

import com.app.appuserservice.dto.LoginDTO;
import com.app.appuserservice.dto.UserAlbumDTO;
import com.app.appuserservice.dto.UserDTO;
import com.app.appuserservice.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
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
        log.info("Getting status");
        final var tokenSecret = env.getProperty("token.secret");
        return ResponseEntity.ok(Map.of(
            "app-name", "users-ms on port: %s".formatted(env.getProperty("local.server.port")),
            "token-secret", tokenSecret));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.save(userDTO));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserAlbumDTO> getUsers(@NotBlank @PathVariable("userId")String userId) {
        log.info("Request to get user by id- {}", userId);
        return ResponseEntity.ok(userService.findByUserId(userId));
    }

    @GetMapping("/{userId}/albums")
    public ResponseEntity<UserAlbumDTO> userAlbums(@PathVariable("userId") String userId) {
        var userAlbums = userService.findByUserId(userId);
        return ResponseEntity.ok(userAlbums);
    }
}
