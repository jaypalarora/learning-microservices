package com.app.appuserservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAlbumDTO {
    private UserDTO userDTO;
    private List<AlbumDTO> albumDTO;
}