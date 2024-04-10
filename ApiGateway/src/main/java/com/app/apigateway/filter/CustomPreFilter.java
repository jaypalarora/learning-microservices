package com.app.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomPreFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        log.info("First custom pre filter executed");

        final var request = exchange.getRequest();
        final var path = request.getPath().toString();
        log.info("Request path = {}", path);

        StringBuilder headersKeyValues = new StringBuilder(128);
        request.getHeaders()
            .forEach((key, value) -> {
                headersKeyValues.append("%s = %s\n".formatted(key, value));
            });

        log.info("Request Headers:\n{}", headersKeyValues);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
