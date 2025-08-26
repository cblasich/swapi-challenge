package com.starwarsapp.services;

import com.starwarsapp.dtos.AuthRequestDTO;
import com.starwarsapp.dtos.AuthResponseDTO;

public interface AuthenticationService {

    AuthResponseDTO authenticateUser(AuthRequestDTO loginRequest);

}
