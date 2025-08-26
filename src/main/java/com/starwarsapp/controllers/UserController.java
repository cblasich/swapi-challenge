package com.starwarsapp.controllers;

import com.starwarsapp.dtos.UserRegisterDTO;
import com.starwarsapp.dtos.UserResponseDTO;
import com.starwarsapp.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody @Valid UserRegisterDTO userDTO) {
        UserResponseDTO createdUser = userService.registerUser(userDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

}
