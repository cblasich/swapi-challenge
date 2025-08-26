package com.starwarsapp.services;

import com.starwarsapp.dtos.UserRegisterDTO;
import com.starwarsapp.dtos.UserResponseDTO;

import java.util.List;

public interface UserService {

    List<UserResponseDTO> findAll();
    UserResponseDTO registerUser(UserRegisterDTO dto);

}
