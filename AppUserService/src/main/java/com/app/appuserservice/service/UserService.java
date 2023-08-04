package com.app.appuserservice.service;

import com.app.appuserservice.dto.UserDTO;
import com.app.appuserservice.entity.UserEntity;
import com.app.appuserservice.repository.UserRepository;
import com.googlecode.jmapper.JMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final JMapper<UserEntity, UserDTO> dtoToEntityMapper = new JMapper<>(UserEntity.class, UserDTO.class);
    private final JMapper<UserDTO, UserEntity> entityToDTOMapper = new JMapper<>(UserDTO.class, UserEntity.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(final UserRepository userRepository, final BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO save(UserDTO userDTO) {
        log.info("Saving user {}", userDTO);
        final var userEntity = dtoToEntityMapper.getDestination(userDTO);
        userEntity.setEncryptedPassword(passwordEncoder.encode(userDTO.getPassword()));
        final var savedUserDTO = entityToDTOMapper.getDestination(userRepository.save(userEntity));
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
        return entityToDTOMapper.getDestination(userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email)));
    }
}
