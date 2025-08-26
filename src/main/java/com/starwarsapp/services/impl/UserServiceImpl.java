package com.starwarsapp.services.impl;

import com.starwarsapp.dtos.UserRegisterDTO;
import com.starwarsapp.dtos.UserResponseDTO;
import com.starwarsapp.entities.Role;
import com.starwarsapp.entities.User;
import com.starwarsapp.exceptions.BadRequestException;
import com.starwarsapp.exceptions.ConflictException;
import com.starwarsapp.mapper.UserMapper;
import com.starwarsapp.repositories.RoleRepository;
import com.starwarsapp.repositories.UserRepository;
import com.starwarsapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserResponseDTO> findAll() {
        return userMapper.toListOfResponseDTO(userRepository.findAll());
    }

    @Override
    public UserResponseDTO registerUser(UserRegisterDTO userRegisterDTO) {

        if (userRepository.findByUsername(userRegisterDTO.getUsername()).isPresent()) {
            throw new ConflictException("El nombre de usuario ya existe.");
        }
        List<Role> roles = roleRepository.findByNameIn(userRegisterDTO.getRoles());
        if (roles.isEmpty()) {
            throw new BadRequestException("No se encontraron roles válidos.");
        }

        User user = userMapper.toEntity(userRegisterDTO, roles, passwordEncoder);
        user = userRepository.save(user);

        return userMapper.toResponseDTO(user);
    }
}
