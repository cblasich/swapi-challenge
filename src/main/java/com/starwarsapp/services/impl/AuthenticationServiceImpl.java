package com.starwarsapp.services.impl;

import com.starwarsapp.dtos.AuthRequestDTO;
import com.starwarsapp.dtos.AuthResponseDTO;
import com.starwarsapp.exceptions.BadRequestException;
import com.starwarsapp.security.JwtUtil;
import com.starwarsapp.services.AuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponseDTO authenticateUser(AuthRequestDTO authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            String token = jwtUtil.generateToken(authentication);
            return new AuthResponseDTO(token, authRequest.getUsername());
        } catch (AuthenticationException ex) {
            throw new BadRequestException("Credenciales invalidas");
        }
    }
}
