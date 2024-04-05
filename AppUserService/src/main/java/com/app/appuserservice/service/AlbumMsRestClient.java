package com.app.appuserservice.service;

import com.app.appuserservice.dto.AlbumDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("albums-ms")
public interface AlbumMsRestClient {

   @GetMapping("/users/{id}/albums")
   List<AlbumDTO> getAlbums(@PathVariable String id);
}
