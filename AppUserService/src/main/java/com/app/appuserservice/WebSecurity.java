package com.app.appuserservice;

import com.app.appuserservice.rest.filter.AuthenticationFilter;
import com.app.appuserservice.service.UserService;
import jakarta.ws.rs.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@Configuration
@EnableWebSecurity
public class WebSecurity {

    private Environment environment;
    private UserService userService;

    public WebSecurity(final Environment environment, final UserService userService) {
        this.environment = environment;
        this.userService = userService;
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(ahr -> {
                ahr.requestMatchers(HttpMethod.POST, "/users")
                    .access(new WebExpressionAuthorizationManager("hasIpAddress(%s)".formatted(environment.getProperty("gateway.ip"))));
            })
            .addFilter(new AuthenticationFilter(authenticationManager(http), userService, environment))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    private AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        final var authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService());
        return authenticationManagerBuilder.build();
    }

    private UserDetailsService userDetailsService() {
        return userService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
