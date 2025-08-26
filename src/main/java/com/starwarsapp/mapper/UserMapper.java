package com.starwarsapp.mapper;

import com.starwarsapp.dtos.UserRegisterDTO;
import com.starwarsapp.dtos.UserResponseDTO;
import com.starwarsapp.entities.Role;
import com.starwarsapp.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toEntity(UserRegisterDTO dto, List<Role> roles, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(roles);
        return user;
    }

    public UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRoles(
                user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    public List<UserResponseDTO> toListOfResponseDTO(List<User> users) {
        return users.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
}
