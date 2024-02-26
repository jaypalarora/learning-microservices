package com.app.appuserservice;

import com.app.appuserservice.rest.filter.AuthenticationFilter;
import com.app.appuserservice.service.UserService;
import jakarta.ws.rs.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurity {

    private Environment environment;
    private UserService userService;
    private final PasswordEncoder passwordEncoder;

    public WebSecurity(final Environment environment, final UserService userService, final PasswordEncoder passwordEncoder) {
        this.environment = environment;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {

        var authManager = authenticationManager(http);

        final var authenticationFilter = new AuthenticationFilter(authManager, userService, environment);
        final var loginUrl = environment.getProperty("login.url.path");
        log.info("Login URL: {}", loginUrl);
        authenticationFilter.setFilterProcessesUrl(loginUrl);

        var gatewayIp = environment.getProperty("gateway.ip");
        log.info("Gateway IP: {}", gatewayIp);

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(ahr ->
                ahr.requestMatchers(HttpMethod.POST, "/users")
                        .access(new WebExpressionAuthorizationManager("hasIpAddress('" + gatewayIp + "')"))
                    .requestMatchers(HttpMethod.GET, "/users/status").permitAll()
                    .requestMatchers(HttpMethod.GET, HttpMethod.DELETE, HttpMethod.PUT, "/users**").permitAll()
                //below allows access to the /users endpoint only from the api gateway IP address.
                    /*.access(new WebExpressionAuthorizationManager("hasIpAddress(%s)".formatted(environment.getProperty("gateway.ip"))))*/
            )
            .addFilter(authenticationFilter)
            .authenticationManager(authManager)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    private AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        final var authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
            .userDetailsService(userService)
            .passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }
}
