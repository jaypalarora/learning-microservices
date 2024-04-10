package com.app.appuserservice.service;

import com.app.appuserservice.dto.AlbumDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("albums-ms")
public interface AlbumMsRestClient {

   Logger log = LoggerFactory.getLogger(AlbumMsRestClient.class);

   @GetMapping("/users/{id}/albums")
   @Retry(name = "albums-ms")
   @CircuitBreaker(name = "albums-ms", fallbackMethod = "getAlbumsFallback")
   //Since both Retry and CircuitBreaker are used from Resilience4J, the default order of execution is CircuitBreaker then Retry.
   List<AlbumDTO> getAlbums(@PathVariable String id);

   default List<AlbumDTO> getAlbumsFallback(String id, Throwable t) {
      log.error("Error getting albums for user {}", id, t.getMessage());
      return List.of();
   }
}
