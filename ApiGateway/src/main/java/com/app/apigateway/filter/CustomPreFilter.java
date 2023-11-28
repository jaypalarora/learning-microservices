package com.app.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomPreFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        log.info("CustomPreFilter invoked");

        final var request = exchange.getRequest();
        final var path = request.getPath().toString();
        log.info("Request path = {}", path);

        request.getHeaders()
            .forEach((key, value) -> {
                log.info("Request header: {} = {}", key, value);
            });

        return chain.filter(exchange);
    }
}
