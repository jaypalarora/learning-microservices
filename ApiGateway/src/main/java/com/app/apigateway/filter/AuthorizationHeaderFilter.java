package com.app.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private static final String BEARER_ = "Bearer ";
    private static final String INVALID_AUTH_HEADER_ERR_MSG = "Invalid Authorization header";

    private Environment environment;

    public AuthorizationHeaderFilter(final Environment environment) {
        super(Config.class);
        this.environment = environment;
    }

    public static class Config {
        //todo add configs here...
    }

    @Override
    public GatewayFilter apply(final Config config) {
        //exchange has http request, auth header etc.
        //chain is to delegate it to the subsequent filters.
        log.debug("Applying AuthorizationHeaderFilter");
        return (exchange, chain) -> {
            final var request = exchange.getRequest();
            final var headers = request.getHeaders();
            if(!headers.containsKey(AUTHORIZATION)) {
                return onError(exchange, "Authorization header is missing", UNAUTHORIZED);
            }

            final var authHeader = headers.getFirst(AUTHORIZATION);
            log.debug("Authorization header: {}", authHeader);
            if(!authHeader.startsWith(BEARER_)) {
                return onError(exchange, INVALID_AUTH_HEADER_ERR_MSG, UNAUTHORIZED);
            }

            final var token = authHeader.substring(BEARER_.length());
            log.debug("Token: {}", token);
            if(token.isBlank()) {
                return onError(exchange, INVALID_AUTH_HEADER_ERR_MSG, UNAUTHORIZED);
            }

            if (!isJwtTokenValid(token)) {
                return onError(exchange, INVALID_AUTH_HEADER_ERR_MSG, UNAUTHORIZED);
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(final ServerWebExchange exchange, final String errMsg, final HttpStatus httpStatus) {
        final var response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        return response.setComplete();
    }

    private boolean isJwtTokenValid(final String token) {
        final var secret = environment.getProperty("token.secret", String.class);
        log.debug("Token secret: {}", secret);
        final var secretBytes = Base64.getEncoder().encode(secret.getBytes());
        final var signingKey = new SecretKeySpec(secretBytes, SignatureAlgorithm.HS512.getJcaName());

        JwtParser jwtParser = Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build();

        final Jwt<Header, Claims> parsedToken;
        try {
            parsedToken = jwtParser.parse(token);
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            log.error("Invalid jwt token. {}", e.getMessage());
            return false;
        }

        final var subject = parsedToken.getBody().getSubject();
        return StringUtils.isNotBlank(subject);
    }
}
