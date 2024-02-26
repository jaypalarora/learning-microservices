package com.app.appalbumservice.shared.mapper;
public interface BaseMapper<E, D> {

    E toEntity(D dto);

    D toDto(E entity);
}
