package com.app.appuserservice.service;

import com.app.appuserservice.dto.UserDTO;
import com.app.appuserservice.repository.UserRepository;
import com.app.appuserservice.shared.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(final UserRepository userRepository, final PasswordEncoder passwordEncoder, final UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public UserDTO save(UserDTO userDTO) {
        log.info("Saving user {}", userDTO);
        final var userEntity = userMapper.toEntity(userDTO);
        userEntity.setEncryptedPassword(passwordEncoder.encode(userDTO.getPassword()));
        userEntity.setUserId(UUID.randomUUID().toString());
        final var savedUserDTO = userMapper.toDto(userRepository.save(userEntity));
        log.info("Saved user {}", savedUserDTO);

        return savedUserDTO;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final var userDetails = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));

        return new User(userDetails.getEmail(), userDetails.getEncryptedPassword(), List.of());
    }

    public UserDTO findByEmail(final String email) {
        return userMapper.toDto(userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email)));
    }
}
