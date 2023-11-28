package com.app.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * Custom pre post filters defined in a single class. {@link GlobalFiltersConfiguration}.
 */
@Slf4j
@Configuration
public class GlobalFiltersConfiguration {

    @Bean
    public GlobalFilter secondCustomPrePostFilter() {
        return (exchange, chain) -> {
            log.info("secondCustomPreFilter executed");
            return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                log.info("secondCustomPostFilter executed");
            }));
        };
    }

    @Bean
    public GlobalFilter thirdCustomPrePostFilter() {
        return (exchange, chain) -> {
            log.info("thirdCustomPreFilter executed");
            return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    log.info("thirdCustomPostFilter executed");
                }));
        };
    }
}
