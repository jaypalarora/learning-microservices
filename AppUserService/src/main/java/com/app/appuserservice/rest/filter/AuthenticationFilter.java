package com.app.appuserservice.rest.filter;

import com.app.appuserservice.dto.LoginDTO;
import com.app.appuserservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private UserService userService;
    private Environment environment;

    public AuthenticationFilter(AuthenticationManager authenticationManager, final UserService userService, final Environment environment) {
        super(authenticationManager);
        this.userService = userService;
        this.environment = environment;
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {
        final var loginDTO = MAPPER.readValue(request.getInputStream(), LoginDTO.class);
        //TODO add validation for username and password?
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) {
        final var username = ((User) authResult.getPrincipal()).getUsername();
        final var userDTO = userService.findByEmail(username);

        final var now = Instant.now();
        final var token = Jwts.builder()
            .setSubject(userDTO.getUserId())
            .setIssuedAt(Date.from(now))
            .setExpiration(getExpiration(now))
            .signWith(getSecretKey())
            .compact();

        response.addHeader("token", token);
        response.addHeader("userId", userDTO.getUserId());
    }

    private Date getExpiration(final Instant now) {
        final var tokenExpiryTime = environment.getProperty("token.expiration", Integer.class);
        log.info("Token Expiration Time: {}", tokenExpiryTime);
        return Date.from(now.plusSeconds(tokenExpiryTime));
    }

    private SecretKey getSecretKey() {
        var secret = environment.getProperty("token.secret");
        log.info("Token Secret: {}", secret);
        byte[] secretBytes = Base64.getEncoder().encode(secret.getBytes());
        return new SecretKeySpec(secretBytes, SignatureAlgorithm.HS512.getJcaName());
    }
}
