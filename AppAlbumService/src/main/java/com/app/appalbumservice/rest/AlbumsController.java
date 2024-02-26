/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.appalbumservice.rest;

import com.app.appalbumservice.dto.AlbumDTO;
import com.app.appalbumservice.entity.AlbumEntity;
import com.app.appalbumservice.service.AlbumService;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@RestController
@RequestMapping("/users/{id}/albums")
public class AlbumsController {
    
    final AlbumService albumsService;

    public AlbumsController(AlbumService albumsService) {
        this.albumsService = albumsService;
    }

    @GetMapping
    public List<AlbumDTO> userAlbums(@PathVariable String id) {
        var albums = albumsService.getAlbums(id);
        log.info("Found %d albums".formatted(albums.size()));
        return albums;
    }
}
