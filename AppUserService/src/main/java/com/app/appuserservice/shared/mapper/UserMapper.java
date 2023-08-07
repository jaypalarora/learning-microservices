package com.app.appuserservice.shared.mapper;

import com.app.appuserservice.dto.UserDTO;
import com.app.appuserservice.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<UserEntity, UserDTO> {
}
