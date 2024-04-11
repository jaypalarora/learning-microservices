package com.app.appuserservice.service;

import com.app.appuserservice.dto.AlbumDTO;
import com.app.appuserservice.dto.UserAlbumDTO;
import com.app.appuserservice.dto.UserDTO;
import com.app.appuserservice.repository.UserRepository;
import com.app.appuserservice.shared.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {


    @Value("${service-urls.album}")
    private String albumServiceUrl;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    AlbumMsRestClient albumMsRestClient;

//    private RestTemplate restTemplate;

    public UserService(final UserRepository userRepository, final PasswordEncoder passwordEncoder, final UserMapper userMapper, AlbumMsRestClient albumMsRestClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
//        this.restTemplate = restTemplate;
        this.albumMsRestClient = albumMsRestClient;
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

    public UserAlbumDTO findByUserId(final String userId) {
        final var userDTO = userMapper.toDto(userRepository.findByUserId(userId).orElseThrow(() -> new UsernameNotFoundException(userId)));

        var albumUrl = albumServiceUrl.formatted(userDTO.getUserId());
        log.debug("User album url: {}", albumUrl);
//        final List<AlbumDTO> albumDTOs = restTemplate.exchange(albumUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumDTO>>() {}).getBody();
        final List<AlbumDTO> albumDTOs = albumMsRestClient.getAlbums(userDTO.getUserId());
        log.debug("Found Albums for userId: {}, albumCount: {}", userId, albumDTOs.size());

        return new UserAlbumDTO(userDTO, albumDTOs);
    }
}
