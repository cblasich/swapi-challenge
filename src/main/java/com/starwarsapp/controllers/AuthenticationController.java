package com.starwarsapp.controllers;

import com.starwarsapp.dtos.AuthRequestDTO;
import com.starwarsapp.dtos.AuthResponseDTO;
import com.starwarsapp.services.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid AuthRequestDTO authRequest) {
        AuthResponseDTO jwtResponse = authenticationService.authenticateUser(authRequest);
        return ResponseEntity.ok(jwtResponse);
    }
}
