package com.app.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

/**
 * Custom pre post filters defined in a single class. {@link GlobalFiltersConfiguration}.
 */
@Slf4j
@Configuration
public class GlobalFiltersConfiguration {

    @Order(1)
    @Bean
    public GlobalFilter secondCustomPrePostFilter() {
        return (exchange, chain) -> {
            log.debug("2nd custom pre filter executed");
            return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                log.debug("2nd custom post filter executed");
            }));
        };
    }

    @Order(2)
    @Bean
    public GlobalFilter thirdCustomPrePostFilter() {
        return (exchange, chain) -> {
            log.debug("3rd custom pre filter executed");
            return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    log.debug("3rd custom post filter executed");
                }));
        };
    }
}
