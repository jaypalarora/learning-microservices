package com.app.appalbumservice.shared.mapper;

import com.app.appalbumservice.entity.AlbumEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AlbumMapper extends BaseMapper<AlbumEntity, com.app.appalbumservice.dto.AlbumDTO> {
}
