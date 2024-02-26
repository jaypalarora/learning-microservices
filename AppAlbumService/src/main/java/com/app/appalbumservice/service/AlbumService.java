package com.app.appalbumservice.service;


import com.app.appalbumservice.dto.AlbumDTO;
import com.app.appalbumservice.shared.mapper.AlbumMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AlbumService {

    final AlbumMapper albumMapper;

    public AlbumService(AlbumMapper albumMapper) {
        this.albumMapper = albumMapper;
    }

    public List<AlbumDTO> getAlbums(String userId) {
        List<AlbumDTO> albumDTOS = new ArrayList<>();

        AlbumDTO albumDTO1 = AlbumDTO.builder()
                .userId(userId)
                .albumId("album1Id1")
                .description("album 1 description")
                .name("album 1 name")
                .build();

        AlbumDTO albumDTO2 = AlbumDTO.builder()
                .userId(userId)
                .albumId("album1Id2")
                .description("album 1 description")
                .name("album 1 name")
                .build();

        albumDTOS.add(albumDTO1);
        albumDTOS.add(albumDTO2);

        return albumDTOS;
    }
}
