package com.app.appuserservice.rest.filter;

import com.app.appuserservice.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UserService userService;
    private Environment environment;

    public AuthenticationFilter(AuthenticationManager authenticationManager, final UserService userService, final Environment environment) {
        super(authenticationManager);
        this.userService = userService;
        this.environment = environment;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        //TODO add validation for username and password?
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(username, password));
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
        return Date.from(now.plusSeconds(environment.getProperty("token.expiration", Integer.class)));
    }

    private SecretKey getSecretKey() {
        var secret = environment.getProperty("token.secret");
        byte[] secretBytes = Base64.getEncoder().encode(secret.getBytes());
        return new SecretKeySpec(secretBytes, SignatureAlgorithm.HS512.getJcaName());
    }
}
